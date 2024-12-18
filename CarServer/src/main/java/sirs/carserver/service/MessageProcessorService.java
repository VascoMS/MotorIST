package sirs.carserver.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.util.JSONUtil;
import sirs.carserver.model.PairingSession;
import sirs.carserver.model.dto.ResponseDto;

@Service
public class MessageProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(MessageProcessorService.class);

    private final PairingService pairingService;

    public MessageProcessorService(PairingService pairingService) {
        this.pairingService = pairingService;
    }

    public ResponseDto processMessage(String message) {
        logger.info("Processing message: {}", message);
        JsonObject messageJson = JSONUtil.parseJson(message);
        String operation = messageJson.get("operation").getAsString();
        boolean success = false;
        switch (operation) {
            case "pair" -> {
                return pairOperation(messageJson);
            }
            case "updateconfig" -> updateConfigOperation(messageJson);
            case "deleteconfig" -> deleteConfigOperation(messageJson);
            case "newuser" -> newUserOperation(messageJson);
            default -> logger.error("Operation not supported: {}", operation);
        }
        return null;
    }

    public ResponseDto pairOperation(JsonObject messageJson) {
        // TODO: Sign all responses to authenticate the car with the server
        PairingSession pairingSession = pairingService.createPairSession();
        if(pairingSession == null) {
            logger.warn("Unable to create new pair session, already in use...");
            return new ResponseDto(false, null);
        }

        return new ResponseDto(true, pairingSession.toJson());
    }

    public void updateConfigOperation(JsonObject messageJson) {

    }

    public void deleteConfigOperation(JsonObject messageJson) {

    }

    public void newUserOperation(JsonObject messageJson) {

    }

}
