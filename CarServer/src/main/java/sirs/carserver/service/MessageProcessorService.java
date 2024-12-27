package sirs.carserver.service;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.model.ProtectedObject;
import pt.tecnico.sirs.secdoc.Protect;
import pt.tecnico.sirs.util.JSONUtil;
import sirs.carserver.consts.WebSocketOpsConsts;
import sirs.carserver.exception.InvalidOperationException;
import sirs.carserver.exception.PairingSessionException;
import sirs.carserver.model.GeneralCarInfo;
import sirs.carserver.model.dto.OpResponseWithContentDto;
import sirs.carserver.model.dto.OpResponseDto;
import sirs.carserver.observer.Observer;
import sirs.carserver.observer.Subject;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageProcessorService implements Subject {
    // TODO: IF WE HAVE TIME, UserId, ReqId and Operation as independent fields, protected object info as sub json
    // TODO: Check nonces from server
    private static final Logger logger = LoggerFactory.getLogger(MessageProcessorService.class);

    private final PairingService pairingService;
    private final UserService userService;
    private final CarInfoService carInfoService;
    private final KeyStoreService keyStoreService;
    private final List<Observer> pairingResultObservers = new ArrayList<>();
    private static final ConcurrentHashMap<String, CompletableFuture<Boolean>> pendingRequests = new ConcurrentHashMap<>();

    public MessageProcessorService(@Lazy PairingService pairingService, UserService userService, CarInfoService carInfoService, KeyStoreService keyStoreService) {
        this.pairingService = pairingService;
        this.userService = userService;
        this.carInfoService = carInfoService;
        this.keyStoreService = keyStoreService;
    }

    public OpResponseDto processMessage(String message) throws InvalidOperationException {
        // Returns null if no response is needed
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
                pairingService.storeKey(userId);
                userService.createUser(userId);
            } catch (PairingSessionException e) {
                logger.error("Error storing the new key: {}", e.getMessage());
                pairResult = false;
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
        String requestId = messageJson.get(WebSocketOpsConsts.REQ_ID).getAsString();

        // Generate new protected object, so I can unprotect it
        ProtectedObject protectedObject = JSONUtil.parseJsonToClass(messageJson, ProtectedObject.class);

        boolean success = userService.updateConfig(username, protectedObject);
        return new OpResponseDto(requestId, success);
    }

    public OpResponseDto deleteConfigOperation(JsonObject messageJson) {
        String username = messageJson.get(WebSocketOpsConsts.USERID_FIELD).getAsString();
        String requestId = messageJson.get(WebSocketOpsConsts.REQ_ID).getAsString();

        ProtectedObject protectedObject = JSONUtil.parseJsonToClass(messageJson, ProtectedObject.class);
        boolean success = userService.deleteConfig(username, protectedObject);
        return new OpResponseDto(requestId, success);
    }

    public OpResponseDto carInfoOperation(JsonObject messageJson){
        String userId = messageJson.get(WebSocketOpsConsts.USERID_FIELD).getAsString();
        String reqId = messageJson.get(WebSocketOpsConsts.REQ_ID).getAsString();
        SecretKeySpec secretKey = keyStoreService.getSecretKeySpec(userId);
        if(secretKey == null){
            logger.error("No secret key found for user: {}", userId);
            return new OpResponseDto(reqId, false);
        }
        GeneralCarInfo carInfo = carInfoService.getCarInfo(userId);
        Protect protect = new Protect();
        try {
            ProtectedObject protectedCarInfo = protect.protect(secretKey, carInfo, false);
            return new OpResponseWithContentDto(reqId, true, protectedCarInfo);
        } catch (IOException e) {
            logger.error("Error protecting car info: {}", e.getMessage());
            return new OpResponseDto(reqId, false);
        }
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

    public CompletableFuture<Boolean> addPendingRequest(String reqId) {
        // Store the CompletableFuture for tracking response
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pendingRequests.put(reqId, future);
        return future;
    }

}
