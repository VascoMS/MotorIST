package sirs.carserver.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.model.ProtectedObject;
import pt.tecnico.sirs.secdoc.Protect;
import sirs.carserver.exception.PairingSessionException;
import sirs.carserver.model.PairingSession;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.PrivateKey;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service
public class PairingService {
    private static final Logger logger = LoggerFactory.getLogger(PairingService.class);

    private PairingSession pairingSession;
    private final HttpClient httpClient;
    private final KeyStoreService keyStoreService;
    private static final long PAIRING_SESSION_TIMEOUT = 2;
    private static final String PAIRING_URI = "http://localhost:8443/car/pair";
    @Value("${car.id}")
    private String carId;


    public PairingService(KeyStoreService keyStoreService) {
        // Schedule a cleanup task to remove expired pair requests
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::cleanupExpiredRequests, 1, 1, TimeUnit.MINUTES);
        this.httpClient = HttpClient.newHttpClient();
        this.keyStoreService = keyStoreService;
    }

    public String createPairingSession() throws PairingSessionException {
        if(pairingSession != null) {
            throw new PairingSessionException("Pairing session already exists.");
        }
        pairingSession = new PairingSession();
        // Creating request to send to manufacturer for pairing code verification
        String jsonPayload = buildJsonPayload(pairingSession);
        if(jsonPayload == null) {
            throw new PairingSessionException("Error building json payload for pairing request.");
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PAIRING_URI))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();
        try {
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            if(response.statusCode() != 200) {
                throw new PairingSessionException("Server returned error status: " + response.statusCode());
            }
            return pairingSession.getCode();
        } catch (Exception e) {
            throw new PairingSessionException("Error sending pairing request: " + e.getMessage(), e);
        }
    }

    private String buildJsonPayload(PairingSession pairingSession) {
        // TODO: We need to create a protected object with the ciphered config, nonce, code, carId and a signature of all these properties
        try {
            // Get the private key
            PrivateKey privateKey = keyStoreService.getPrivateKey(carId);
            Protect protector = new Protect();
            // Additional fields to be included in the protected object
            Map<String, String> additionalFields = Map.of("carId", carId, "code", pairingSession.getCode());
            ProtectedObject protectedObject = protector.protect(pairingSession.getSecretKey(), pairingSession.getDefaultConfig(), privateKey, additionalFields);
            Gson gson = new Gson();
            return gson.toJson(protectedObject);
        } catch (Exception e) {
            logger.error("Error preparing and signing session payload: {}", e.getMessage());
            return null;
        }
    }

    public boolean checkPairingSession(String code) {
        if(pairingSession == null) {
            return false;
        }
        return pairingSession.getCode().equals(code);
    }

    public void endPairSession() {
        pairingSession = null;
    }

    private void cleanupExpiredRequests() {
        long now = System.currentTimeMillis();
        if(pairingSession != null && (now - pairingSession.getTimestamp()) > TimeUnit.MINUTES.toMillis(PAIRING_SESSION_TIMEOUT)) {
            endPairSession();
        }
    }
}

