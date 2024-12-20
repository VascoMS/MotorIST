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

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CarWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(CarWebSocketHandler.class);

    private final ConcurrentHashMap<String, WebSocketSession> carSessions = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, CompletableFuture<Boolean>> pendingRequests = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        carSessions.put(session.getId(), session);
        System.out.println("Car connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received from car: " + message.getPayload());
        // Send a command to the car

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        carSessions.remove(session.getId());
        System.out.println("Car disconnected: " + session.getId());
    }

    public CompletableFuture<Boolean> sendCommandToCar(String carId, JsonObject jsonObj) {
        // TODO: Add reqId to message
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
}