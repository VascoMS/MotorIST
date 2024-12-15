package sirs.carserver.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.util.JSONUtil;

@Service
public class MessageProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(MessageProcessorService.class);

    public MessageProcessorService() {
    }

    public void processMessage(String message) {
        logger.info("Processing message: {}", message);
        JsonObject messageJson = JSONUtil.parseJson(message);
        String operation = messageJson.get("operation").getAsString();

        switch (operation) {
            case "pair":
                // TODO: Implement pair
                break;
            case "updateconfig":
                // TODO: Implement updateconfig
                break;
            case "deleteconfig":
                // TODO: Implement deleteconfig;
                break;
            case "newuser":
                // TODO: Implement newuser
                break;
            default:
                logger.error("Operation not supported: {}", operation);
        }
    }

    public boolean pairOperation(JsonObject messageJson) {
        return false;
    }

    public boolean updateConfigOperation(JsonObject messageJson) {
        return false;
    }

    public boolean deleteConfigOperation(JsonObject messageJson) {
        return false;
    }

    public boolean newUserOperation(JsonObject messageJson) {
        return false;
    }

}
