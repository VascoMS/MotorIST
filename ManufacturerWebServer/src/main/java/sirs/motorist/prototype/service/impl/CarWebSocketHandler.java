package sirs.motorist.prototype.service.impl;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;
import pt.tecnico.sirs.util.JSONUtil;
import sirs.motorist.prototype.consts.WebSocketOpsConsts;
import sirs.motorist.prototype.service.PairingService;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CarWebSocketHandler extends TextWebSocketHandler {
    // TODO: Maybe the query param in the uri doesn't work
    // TODO: Add nonces to requests which still don't have them
    private static final Logger logger = LoggerFactory.getLogger(CarWebSocketHandler.class);

    private final ConcurrentHashMap<String, WebSocketSession> wsSessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> carSessions = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, CompletableFuture<Boolean>> pendingRequests = new ConcurrentHashMap<>();
    private final PairingService pairingService;

    public CarWebSocketHandler(@Lazy PairingService pairingService){
        this.pairingService = pairingService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String uri = Objects.requireNonNull(session.getUri()).toString();
        String carId = UriComponentsBuilder.fromUriString(uri)
                .build()
                .getQueryParams()
                .getFirst("carId");
        if (carId == null){
            try {
                logger.error("Car id not found in uri, closing session...");
                session.close();
            } catch (IOException e) {
                logger.error("Error closing session: {}", e.getMessage());
            }
            return;
        }
        wsSessions.put(session.getId(), session);
        carSessions.put(carId, session.getId());
        logger.info("Car with id {} connected", carId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println("Received message from car: " + message.getPayload());
        JsonObject messageJson = JSONUtil.parseJson(message.getPayload());
        String operation = messageJson.get(WebSocketOpsConsts.OPERATION_FIELD).getAsString();
        switch (operation) {
            case "initpair" -> initPairOp(messageJson);
            case "response" -> handleResponseOp(messageJson);
        }
    }

    public void initPairOp(JsonObject messageJson){
        logger.info("Received initpair operation");
        String reqId = messageJson.get(WebSocketOpsConsts.REQ_ID).getAsString();
        String carId = messageJson.get(WebSocketOpsConsts.CARID_FIELD).getAsString();
        String code = messageJson.get(WebSocketOpsConsts.CODE_FIELD).getAsString();
        pairingService.initPairingSession(carId, code);
        JsonObject response = new JsonObject();
        response.addProperty(WebSocketOpsConsts.REQ_ID, reqId);
        response.addProperty(WebSocketOpsConsts.OPERATION_FIELD, "initpair-response");
        response.addProperty(WebSocketOpsConsts.SUCCESS_FIELD, true);
        // Send response to car
        sendMessageToCarNoResponse(carId, response);
    }

    public void handleResponseOp(JsonObject messageJson){
        logger.info("Received response operation");
        String reqId = messageJson.get(WebSocketOpsConsts.REQ_ID).getAsString();
        boolean success = Boolean.parseBoolean(messageJson.get(WebSocketOpsConsts.  SUCCESS_FIELD).getAsString());
        CompletableFuture<Boolean> pendingRequest = pendingRequests.get(reqId);
        if(pendingRequest != null) {
            pendingRequest.complete(success);
        } else {
            logger.error("No pending request matches response for reqId: {}", reqId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String carId = carSessions.keySet().stream().filter(key -> carSessions.get(key).equals(session.getId()))
                .findFirst().orElse("Unknown");
        logger.info("Car disconnected: {}", carId);
        wsSessions.remove(session.getId());
        carSessions.values().remove(session.getId());
    }

    public CompletableFuture<Boolean> sendMessageToCarWithResponse(String carId, JsonObject jsonObj) {
        WebSocketSession session = getCarSession(carId);
        if (session != null) {
            String requestId = String.valueOf(System.currentTimeMillis());
            // Store the CompletableFuture for tracking response
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            pendingRequests.put(requestId, future);
            jsonObj.addProperty("reqid", requestId);
            String command = JSONUtil.parseClassToJsonString(jsonObj);
            try {
                session.sendMessage(new TextMessage(command));
                return future;
            } catch (IOException e) {
                logger.error("Error sending message to car: {}", e.getMessage());
                return null;
            }
        }
        return null;
    }

    public void sendMessageToCarNoResponse(String carId, JsonObject jsonObj) {
        WebSocketSession session = getCarSession(carId);
        if (session != null) {
            String command = JSONUtil.parseClassToJsonString(jsonObj);
            try {
                session.sendMessage(new TextMessage(command));
            } catch (IOException e) {
                logger.error("Error sending message to car: {}", e.getMessage());
            }
        }
    }

    private WebSocketSession getCarSession(String carId){
        String wsSessionId = carSessions.get(carId);
        if(wsSessionId != null){
            return wsSessions.get(wsSessionId);
        }
        return null;
    }
}