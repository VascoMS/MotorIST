package sirs.carserver.config;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sirs.carserver.service.MessageProcessorService;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;


public class CarWebSocketClient extends WebSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(CarWebSocketClient.class);

    private static final int MAX_RETRIES = 5; // Maximum reconnection attempts
    private int reconnectAttempts = 0;
    private Timer reconnectTimer;
    private final MessageProcessorService messageProcessorService;

    public CarWebSocketClient(URI serverUri, MessageProcessorService messageProcessorService) {
        super(serverUri);
        this.messageProcessorService = messageProcessorService;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        logger.info("WebSocket connection opened...");
        send("Hello from the car!"); // Send message after connection is established
    }

    @Override
    public void onMessage(String message) {
        logger.info("Received message from server...");
        messageProcessorService.processMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.warn("Connection closed: {}", reason);
        attemptReconnect();
    }

    @Override
    public void onError(Exception ex) {
        logger.error("Error whilst connection was open: {}", ex.getMessage());
        attemptReconnect();
    }

    private void attemptReconnect() {
        if (reconnectAttempts < MAX_RETRIES) {
            reconnectAttempts++;
            int reconnectDelay = 1000 * reconnectAttempts; // Exponential backoff
            logger.info("Reconnecting in " + reconnectDelay / 1000 + " seconds...");

            // Schedule the reconnection attempt after the delay
            if (reconnectTimer != null) {
                reconnectTimer.cancel();  // Cancel any previous reconnection attempts
            }
            reconnectTimer = new Timer();
            // Scheduling
            reconnectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    logger.info("Attempting to reconnect...");
                    try {
                        reconnect();
                    } catch (Exception e) {
                        logger.error("Exception thrown when trying to reconnect: {}", e.getMessage());
                    }
                }
            }, reconnectDelay);
        } else {
            logger.error("Max reconnect attempts reached. Giving up.");
        }
    }



}
