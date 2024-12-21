package sirs.carserver.service;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.util.JSONUtil;
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

    public boolean processMessage(String message) {
        // TODO: Fix fucked up logic where we use the output of this method to determine the reponse but response operation doesn't require a response to be sent
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

    public boolean pairOperation(JsonObject messageJson) {
        String code = messageJson.get(CODE_FIELD).getAsString();
        boolean success = Boolean.parseBoolean(messageJson.get(SUCCESS_FIELD).getAsString());
        boolean codeMatches = pairingService.checkPairingSession(code);
        if(codeMatches && success){
            logger.info("Pairing code: {}", code);
            String userId = messageJson.get(USERID_FIELD).getAsString();
            try {
                userService.createUser(userId);
            } catch (IOException e) {
                logger.error("Error creating user: {}", e.getMessage());
                return false;
            }
        } else {
            // Code sent by server doesn't match current pairing session code or user input was incorrect
            logger.info("Server code matches: {} | User input matches: {}", codeMatches, success);
        }
        notifyObservers(success && codeMatches);
        pairingService.endPairSession();
        return success && codeMatches;
    }

    public boolean updateConfigOperation(JsonObject messageJson) {
        return true;
    }

    public boolean deleteConfigOperation(JsonObject messageJson) {
        return true;
    }

    public boolean newUserOperation(JsonObject messageJson) {
        return true;
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
