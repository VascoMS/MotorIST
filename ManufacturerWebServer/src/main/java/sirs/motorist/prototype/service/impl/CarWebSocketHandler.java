package sirs.motorist.prototype.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class CarWebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, WebSocketSession> carSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        carSessions.put(session.getId(), session);
        System.out.println("Car connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received from car: " + message.getPayload());
        // Send a command to the car
        session.sendMessage(new TextMessage("Hello from manufacturer!"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        carSessions.remove(session.getId());
        System.out.println("Car disconnected: " + session.getId());
    }

    public boolean sendCommandToCar(String carId, String command) {
        WebSocketSession session = carSessions.get(carId);
        if (session != null) {
            try {
                session.sendMessage(new TextMessage(command));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}