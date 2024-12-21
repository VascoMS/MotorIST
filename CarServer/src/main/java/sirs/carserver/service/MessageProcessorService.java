package sirs.carserver.service;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.model.Nonce;
import pt.tecnico.sirs.model.ProtectedObject;
import pt.tecnico.sirs.util.JSONUtil;
import pt.tecnico.sirs.util.SecurityUtil;
import sirs.carserver.exception.InvalidOperationException;
import sirs.carserver.model.dto.OperationResponseDto;
import sirs.carserver.observer.Observer;
import sirs.carserver.observer.Subject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageProcessorService implements Subject {

    private static final Logger logger = LoggerFactory.getLogger(MessageProcessorService.class);

    private static final String REQ_ID = "reqId";
    private static final String IV_FIELD = "iv";
    private static final String HMAC_FIELD = "hmac";
    private static final String CODE_FIELD = "code";
    private static final String NONCE_FIELD = "nonce";
    private static final String USERID_FIELD = "userId";
    private static final String SUCCESS_FIELD = "success";
    private static final String OPERATION_FIELD = "operation";
    private static final String CONFIGURATION_FIELD = "configuration";

    private final PairingService pairingService;
    private final UserService userService;
    private final List<Observer> pairingResultObservers = new ArrayList<>();
    private static final ConcurrentHashMap<String, CompletableFuture<Boolean>> pendingRequests = new ConcurrentHashMap<>();

    public MessageProcessorService(PairingService pairingService, UserService userService) {
        this.pairingService = pairingService;
        this.userService = userService;
    }

    public OperationResponseDto processMessage(String message) throws InvalidOperationException {
        //
        logger.info("Processing message: {}", message);
        JsonObject messageJson = JSONUtil.parseJson(message);
        String operation = messageJson.get(OPERATION_FIELD).getAsString();
        return switch (operation) {
            case "pair" -> {
                pairOperation(messageJson);
                yield null;
            }
            case "updateconfig" -> updateConfigOperation(messageJson);
            case "deleteconfig" -> deleteConfigOperation(messageJson);
            case "initpair-response" -> {
                handleServerResponse(messageJson);
                yield null;
            }
            default -> {
                logger.error("Operation not supported: {}", operation);
                throw new InvalidOperationException("Invalid operation: " + operation);
            }
        };
    }

    public void pairOperation(JsonObject messageJson) {
        String code = messageJson.get(CODE_FIELD).getAsString();
        boolean success = Boolean.parseBoolean(messageJson.get(SUCCESS_FIELD).getAsString());
        boolean codeMatches = pairingService.checkPairingSession(code);
        boolean pairResult = success && codeMatches;
        if(pairResult){
            logger.info("Pairing code: {}", code);
            String userId = messageJson.get(USERID_FIELD).getAsString();
            try {
                userService.createUser(userId);
            } catch (IOException e) {
                logger.error("Error creating user: {}", e.getMessage());
                pairResult = false;
            }
        } else {
            // Code sent by server doesn't match current pairing session code or user input was incorrect
            logger.info("Server code matches: {} | User input matches: {}", codeMatches, success);
        }
        notifyObservers(pairResult);
        pairingService.endPairSession();
    }

    public OperationResponseDto updateConfigOperation(JsonObject messageJson) {
        //Get items from messageJson
        String username = messageJson.get(USERID_FIELD).getAsString();
        String protectedConfiguration = messageJson.get(CONFIGURATION_FIELD).getAsString();
        String iv = messageJson.get(IV_FIELD).getAsString();
        Nonce nonce = JSONUtil.parseJsonToClass(messageJson.get(NONCE_FIELD).getAsJsonObject(), Nonce.class);
        String hmac = messageJson.get(HMAC_FIELD).getAsString();
        String requestId = messageJson.get(REQ_ID).getAsString();

        //Generate new protected object, so I can unprotect it
        ProtectedObject protectedObject = new ProtectedObject(protectedConfiguration, iv, nonce, hmac);

        //Generate new nonce for the operationResponseDTO
        Nonce responseNonce = SecurityUtil.generateNonce(SecurityUtil.RECOMMENDED_NONCE_LENGTH);

        boolean success = userService.updateConfig(username, protectedObject, protectedConfiguration, iv);
        return new OperationResponseDto(requestId, success, responseNonce);
    }

    public OperationResponseDto deleteConfigOperation(JsonObject messageJson) {
        return null;
    }

    public void handleServerResponse(JsonObject message) {
        String reqId = message.get(REQ_ID).getAsString();
        boolean success = Boolean.parseBoolean(message.get(SUCCESS_FIELD).getAsString());
        CompletableFuture<Boolean> pendingRequest = pendingRequests.get(reqId);
        if(pendingRequest != null) {
            pendingRequest.complete(success);
        } else {
            logger.error("No pending request matches response for reqId: {}", reqId);
        }
    }


    @Override
    public void addObserver(Observer observer) {
        pairingResultObservers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        pairingResultObservers.remove(observer);
    }

    @Override
    public void notifyObservers(boolean pairingSuccess) {
        for (Observer observer : pairingResultObservers) {
            observer.update(pairingSuccess);
        }
    }

    public CompletableFuture<Boolean> addPendingRequest() {
        String requestId = String.valueOf(System.currentTimeMillis());

        // Store the CompletableFuture for tracking response
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);
        return future;
    }

}
