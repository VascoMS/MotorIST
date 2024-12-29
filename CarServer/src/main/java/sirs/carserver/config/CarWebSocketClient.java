package sirs.carserver.config;

import com.google.gson.JsonObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.tecnico.sirs.util.JSONUtil;
import sirs.carserver.consts.WebSocketOpsConsts;
import sirs.carserver.exception.InvalidOperationException;
import sirs.carserver.model.dto.OpResponseDto;
import sirs.carserver.service.MessageProcessorService;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;


public class CarWebSocketClient extends WebSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(CarWebSocketClient.class);

    private static final int MAX_RETRIES = 5; // Maximum reconnection attempts
    private int reconnectAttempts = 0;
    private Timer reconnectTimer;
    private final MessageProcessorService messageProcessorService;

    public CarWebSocketClient(URI serverUri, MessageProcessorService messageProcessorService, SSLContext sslContext) {
        super(serverUri);
        this.messageProcessorService = messageProcessorService;
        this.setSocketFactory(sslContext.getSocketFactory());
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        logger.info("WebSocket connection opened...");
    }

    @Override
    public void onMessage(String message) {
        logger.info("Received message from server...");
        try {
            OpResponseDto response = messageProcessorService.processMessage(message);
            if(response != null) {
                send(JSONUtil.parseClassToJsonString(response));
            }
        } catch(InvalidOperationException e) {
            logger.error("Received invalid operation");
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.warn("Connection closed: {}", reason);
        attemptReconnect();
    }

    @Override
    public void onError(Exception ex) {
        logger.error("Error whilst connection was open: {}", ex.getMessage());
        System.out.println("Error when connecting to server, trying again..." );
        attemptReconnect();
    }

    public CompletableFuture<Boolean> sendRequest(JsonObject payload) {
        String reqId = String.valueOf(System.currentTimeMillis());
        payload.addProperty(WebSocketOpsConsts.REQ_ID, reqId);
        CompletableFuture<Boolean> future = messageProcessorService.addPendingRequest(reqId);
        // Send the message
        send(payload.toString());
        return future;
    }

    private void attemptReconnect() {
        if (reconnectAttempts < MAX_RETRIES) {
            reconnectAttempts++;
            int reconnectDelay = 1000 * reconnectAttempts; // Exponential backoff
            logger.info("Reconnecting in {} seconds...", reconnectDelay / 1000);

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
            logger.error("Max reconnect attempts reached. Giving up and shutting down...");
            System.exit(1);
        }
    }



}
