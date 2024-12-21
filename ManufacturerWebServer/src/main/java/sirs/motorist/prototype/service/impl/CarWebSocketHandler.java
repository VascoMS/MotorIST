package sirs.motorist.prototype.service.impl;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pt.tecnico.sirs.util.JSONUtil;
import sirs.motorist.prototype.consts.WebSocketOpsConsts;
import sirs.motorist.prototype.service.PairingService;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CarWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(CarWebSocketHandler.class);

    private final ConcurrentHashMap<String, WebSocketSession> carSessions = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, CompletableFuture<Boolean>> pendingRequests = new ConcurrentHashMap<>();
    private final PairingService pairingService;

    public CarWebSocketHandler(PairingService pairingService){
        this.pairingService = pairingService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        carSessions.put(session.getId(), session);
        System.out.println("Car connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println("Received from car: " + message.getPayload());
        JsonObject messageJson = JSONUtil.parseJson(message.getPayload());
        String operation = messageJson.get(WebSocketOpsConsts.OPERATION_FIELD).getAsString();
        switch (operation){
            case "initpair":
                initPairOp(messageJson);
                break;
            case "response":
                handleResponseOp(messageJson);
                break;
        }


    }

    public boolean initPairOp(JsonObject messageJson){
        // TODO: Finish implementing
        String reqId = messageJson.get(WebSocketOpsConsts.REQ_ID).getAsString();
        String carId = messageJson.get(WebSocketOpsConsts.CARID_FIELD).getAsString();
        String code = messageJson.get(WebSocketOpsConsts.CODE_FIELD).getAsString();
        pairingService.initPairingSession(carId, code);
        // Send response to car
        return true;
    }

    public void handleResponseOp(JsonObject messageJson){
        //TODO: Implement
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        carSessions.remove(session.getId());
        System.out.println("Car disconnected: " + session.getId());
    }

    public CompletableFuture<Boolean> sendMessageToCarWithResponse(String carId, JsonObject jsonObj) {
        WebSocketSession session = carSessions.get(carId);
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
                logger.error("Error sending command to car: {}", e.getMessage());
                return null;
            }
        }
        return null;
    }

    public void sendMessageToCarNoResponse(String carId, JsonObject jsonObj) {
        WebSocketSession session = carSessions.get(carId);
        if (session != null) {
            String command = JSONUtil.parseClassToJsonString(jsonObj);
            try {
                session.sendMessage(new TextMessage(command));
            } catch (IOException e) {
                logger.error("Error sending command to car: {}", e.getMessage());
            }
        }
    }
}