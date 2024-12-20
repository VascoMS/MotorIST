package sirs.carserver.service;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.util.JSONUtil;

@Service
public class MessageProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(MessageProcessorService.class);

    private static final String CODE_FIELD = "code";
    private static final String USERID_FIELD = "userId";
    private static final String SUCCESS_FIELD = "success";

    private final PairingService pairingService;
    private final UserService userService;

    public MessageProcessorService(PairingService pairingService, UserService userService) {
        this.pairingService = pairingService;
        this.userService = userService;
    }

    public void processMessage(String message) {
        logger.info("Processing message: {}", message);
        JsonObject messageJson = JSONUtil.parseJson(message);
        String operation = messageJson.get("operation").getAsString();
        boolean success = false;
        switch (operation) {
            case "pair" -> pairOperation(messageJson);
            case "updateconfig" -> updateConfigOperation(messageJson);
            case "deleteconfig" -> deleteConfigOperation(messageJson);
            case "newuser" -> newUserOperation(messageJson);
            default -> logger.error("Operation not supported: {}", operation);
        }
    }

    public void pairOperation(JsonObject messageJson) {
        String pairingId = messageJson.get(CODE_FIELD).getAsString();
        if(pairingService.checkPairingSession(pairingId)){
            logger.info("Pairing code: {}", pairingId);
            String userId = messageJson.get(USERID_FIELD).getAsString();
            boolean success = Boolean.parseBoolean(messageJson.get(SUCCESS_FIELD).getAsString());
            notifyObservers(success);
            //userService.createUser()
        } else {
            logger.error("Invalid pairing code: {}", pairingId);
        }
        pairingService.endPairSession();
    }

    public void updateConfigOperation(JsonObject messageJson) {

    }

    public void deleteConfigOperation(JsonObject messageJson) {

    }

    public void newUserOperation(JsonObject messageJson) {

    }

}
