package sirs.carserver.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.model.ProtectedObject;
import pt.tecnico.sirs.secdoc.Protect;
import pt.tecnico.sirs.util.JSONUtil;
import sirs.carserver.config.CarWebSocketClient;
import sirs.carserver.consts.WebSocketOpsConsts;
import sirs.carserver.exception.PairingSessionException;
import sirs.carserver.model.PairingSession;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service
public class PairingService {
    private static final Logger logger = LoggerFactory.getLogger(PairingService.class);

    private PairingSession pairingSession;
    private final CarWebSocketClient carWebSocketClient;
    private final KeyStoreService keyStoreService;
    private static final long PAIRING_SESSION_TIMEOUT = 2;
    @Value("${car.id}")
    private String carId;



    public PairingService(KeyStoreService keyStoreService, CarWebSocketClient carWebSocketClient) {
        // Schedule a cleanup task to remove expired pair requests
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::cleanupExpiredRequests, 1, 1, TimeUnit.MINUTES);
        this.keyStoreService = keyStoreService;
        this.carWebSocketClient = carWebSocketClient;
    }

    public String createPairingSession() throws PairingSessionException {
        if(pairingSession != null) {
            throw new PairingSessionException("Pairing session already exists.");
        }
        PairingSession newPairingSession = new PairingSession();
        // Creating request to send to manufacturer for pairing code verification
        JsonObject jsonPayload = buildJsonPayload(newPairingSession);
        if(jsonPayload == null) {
            throw new PairingSessionException("Error building json payload for pairing request.");
        }
        try {
            boolean success = carWebSocketClient.sendRequest(jsonPayload).get();
            if(!success) {
                throw new PairingSessionException("Error sending pairing request.");
            }
            pairingSession = newPairingSession;
            return pairingSession.getCode();
        } catch (Exception e) {
            throw new PairingSessionException("Error sending pairing request: " + e.getMessage(), e);
        }
    }

    private JsonObject buildJsonPayload(PairingSession pairingSession) {
        // TODO: Add nonce to payload.
        try {
            Protect protector = new Protect();
            // Additional fields to be included in the protected object
            Map<String, String> additionalFields = Map.of(WebSocketOpsConsts.OPERATION_FIELD,WebSocketOpsConsts.INITPAIR_OP,
                    WebSocketOpsConsts.CARID_FIELD, carId, WebSocketOpsConsts.CODE_FIELD, pairingSession.getCode());
            ProtectedObject protectedObject = protector.protect(pairingSession.getSecretKey(), pairingSession.getDefaultConfig(), true);
            return JSONUtil.mapToJsonObject(additionalFields, protectedObject);
        } catch (Exception e) {
            logger.error("Error preparing and signing session payload: {}", e.getMessage());
            return null;
        }
    }

    public boolean checkPairingSession(String code) {
        if(pairingSession == null) {
            logger.info("No active pairing session...");
            return false;
        }
        byte[] codeBytes = Base64.getDecoder().decode(code);
        return pairingSession.validateCode(codeBytes);
    }

    public boolean hasActivePairingSession(){
        return pairingSession != null;
    }

    public void endPairSession() {
        pairingSession = null;
    }

    private void cleanupExpiredRequests() {
        long now = System.currentTimeMillis();
        if(pairingSession != null && (now - pairingSession.getTimestamp()) > TimeUnit.MINUTES.toMillis(PAIRING_SESSION_TIMEOUT)) {
            System.out.println("Pairing session timed out...");
            endPairSession();
        }
    }

    public String storeKey() throws PairingSessionException {
        if(pairingSession == null) {
            throw new PairingSessionException("No active pairing session...");
        }
        try {
            keyStoreService.storeNewKey(pairingSession.getSecretKey(), carId);
            return Base64.getEncoder().encodeToString(pairingSession.getSecretKey().getEncoded());
        } catch (Exception e) {
            logger.error("Error storing key: {}", e.getMessage());
            throw new PairingSessionException("Error storing key...");
        }
    }
}

