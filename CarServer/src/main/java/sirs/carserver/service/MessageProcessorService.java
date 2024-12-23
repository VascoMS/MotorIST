package sirs.carserver.service;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.model.Nonce;
import pt.tecnico.sirs.model.ProtectedObject;
import pt.tecnico.sirs.util.JSONUtil;
import sirs.carserver.consts.WebSocketOpsConsts;
import sirs.carserver.exception.InvalidOperationException;
import sirs.carserver.model.dto.OpResponseDto;
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

    private final PairingService pairingService;
    private final UserService userService;
    private final List<Observer> pairingResultObservers = new ArrayList<>();
    private static final ConcurrentHashMap<String, CompletableFuture<Boolean>> pendingRequests = new ConcurrentHashMap<>();

    public MessageProcessorService(PairingService pairingService, UserService userService) {
        this.pairingService = pairingService;
        this.userService = userService;
    }

    public OpResponseDto processMessage(String message) throws InvalidOperationException {
        //
        logger.info("Processing message: {}", message);
        JsonObject messageJson = JSONUtil.parseJson(message);
        String operation = messageJson.get(WebSocketOpsConsts.OPERATION_FIELD).getAsString();
        return switch (operation) {
            case WebSocketOpsConsts.PAIR_OP -> {
                pairOperation(messageJson);
                yield null;
            }
            case WebSocketOpsConsts.UPDATECONFIG_OP -> updateConfigOperation(messageJson);
            case WebSocketOpsConsts.DELETECONFIG_OP -> deleteConfigOperation(messageJson);
            case WebSocketOpsConsts.INITPAIRRESPONSE_OP -> {
                handleServerResponse(messageJson);
                yield null;
            }
            case WebSocketOpsConsts.GENERALCARINFO_OP -> carInfoOperation(messageJson);
            default -> {
                logger.error("Operation not supported: {}", operation);
                throw new InvalidOperationException("Invalid operation: " + operation);
            }
        };
    }

    public void pairOperation(JsonObject messageJson) {
        String code = messageJson.get(WebSocketOpsConsts.CODE_FIELD).getAsString();
        boolean success = Boolean.parseBoolean(messageJson.get(WebSocketOpsConsts.SUCCESS_FIELD).getAsString());
        boolean codeMatches = pairingService.checkPairingSession(code);
        boolean pairResult = success && codeMatches;
        if(pairResult){
            logger.info("Pairing code: {}", code);
            String userId = messageJson.get(WebSocketOpsConsts.USERID_FIELD).getAsString();
            try {
                userService.createUser(userId);
            } catch (IOException e) {
                logger.error("Error creating user: {}", e.getMessage());
                pairResult = false;
            }
        } else {
            // Code sent by server doesn't match current pairing session code or user input was incorrect
            logger.info("Pair failed: Server code matches: {} | User input matches: {}", codeMatches, success);
        }
        notifyObservers(pairResult);
        pairingService.endPairSession();
    }

    public OpResponseDto updateConfigOperation(JsonObject messageJson) {
        //Get items from messageJson
        String username = messageJson.get(WebSocketOpsConsts.USERID_FIELD).getAsString();
        String protectedConfiguration = messageJson.get(WebSocketOpsConsts.CONFIGURATION_FIELD).getAsString();
        String iv = messageJson.get(WebSocketOpsConsts.IV_FIELD).getAsString();
        Nonce nonce = JSONUtil.parseJsonToClass(messageJson.get(WebSocketOpsConsts.NONCE_FIELD).getAsJsonObject(), Nonce.class);
        String hmac = messageJson.get(WebSocketOpsConsts.HMAC_FIELD).getAsString();
        String requestId = messageJson.get(WebSocketOpsConsts.REQ_ID).getAsString();

        //Generate new protected object, so I can unprotect it
        ProtectedObject protectedObject = new ProtectedObject(protectedConfiguration, iv, nonce, hmac);

        boolean success = userService.updateConfig(username, protectedObject, protectedConfiguration, iv);
        return new OpResponseDto(requestId, success);
    }

    public OpResponseDto deleteConfigOperation(JsonObject messageJson) {
        //TODO: implement delete
        return null;
    }

    public OpResponseDto carInfoOperation(JsonObject messageJson){
        //TODO: Implement
        return null;
    }

    public void handleServerResponse(JsonObject message) {
        String reqId = message.get(WebSocketOpsConsts.REQ_ID).getAsString();
        boolean success = Boolean.parseBoolean(message.get(WebSocketOpsConsts.  SUCCESS_FIELD).getAsString());
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
