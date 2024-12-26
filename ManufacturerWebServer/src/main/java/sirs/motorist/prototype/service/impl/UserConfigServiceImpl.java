package sirs.motorist.prototype.service.impl;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.util.JSONUtil;
import sirs.motorist.prototype.consts.WebSocketOpsConsts;
import sirs.motorist.prototype.model.dto.ConfigurationDto;
import sirs.motorist.prototype.model.dto.DeleteConfigDto;
import sirs.motorist.prototype.model.entity.Configuration;
import sirs.motorist.prototype.repository.ConfigRepository;
import sirs.motorist.prototype.service.UserConfigService;

@Service
public class UserConfigServiceImpl implements UserConfigService {

    private static final Logger logger = LoggerFactory.getLogger(UserConfigServiceImpl.class);

    private final ConfigRepository configRepository;
    private final CarWebSocketHandler carWebSocketHandler;

    @Autowired
    public UserConfigServiceImpl(ConfigRepository configRepository, CarWebSocketHandler carWebSocketHandler) {
        this.configRepository = configRepository;
        this.carWebSocketHandler = carWebSocketHandler;
    }

    @Override
    public Configuration getConfiguration(String userId, String carId) {
        return configRepository.findByUserIdAndCarId(userId, carId);
    }

    @Override
    public Boolean updateConfiguration(ConfigurationDto request) {
        JsonObject jsonObj = new JsonObject();
        String nonce = JSONUtil.parseClassToJsonString(request.getNonce());
        jsonObj.addProperty(WebSocketOpsConsts.OPERATION_FIELD, WebSocketOpsConsts.UPDATECONFIG_OP);
        jsonObj.addProperty(WebSocketOpsConsts.USERID_FIELD, request.getUserId());
        jsonObj.addProperty(WebSocketOpsConsts.CONTENT_FIELD, request.getConfiguration());
        jsonObj.addProperty(WebSocketOpsConsts.IV_FIELD, request.getIv());
        jsonObj.addProperty(WebSocketOpsConsts.NONCE_FIELD, nonce);
        jsonObj.addProperty(WebSocketOpsConsts.HMAC_FIELD, request.getHmac());
        try {
            JsonObject response = carWebSocketHandler.sendMessageToCarWithResponse(request.getCarId(), jsonObj).get();
            boolean success = carWebSocketHandler.checkSuccess(response);
            if(success) {
                Configuration config = new Configuration(
                        request.getUserId(),
                        request.getCarId(),
                        request.getConfiguration(),
                        request.getIv(),
                        request.getNonce(),
                        request.getHmac()
                );
                configRepository.save(config);
            }
            return success;
        } catch (Exception e) {
            logger.error("Failed to update config: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean deleteConfiguration(DeleteConfigDto request) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(WebSocketOpsConsts.OPERATION_FIELD, WebSocketOpsConsts.DELETECONFIG_OP);
        jsonObj.addProperty(WebSocketOpsConsts.USERID_FIELD, request.getUserId());
        jsonObj.addProperty(WebSocketOpsConsts.CONTENT_FIELD, request.getConfirmationPhrase());
        jsonObj.addProperty(WebSocketOpsConsts.IV_FIELD, request.getIv());
        jsonObj.addProperty(WebSocketOpsConsts.NONCE_FIELD, JSONUtil.parseClassToJsonString(request.getNonce()));
        jsonObj.addProperty(WebSocketOpsConsts.HMAC_FIELD, request.getHmac());
        try {
            JsonObject response = carWebSocketHandler.sendMessageToCarWithResponse(request.getCarId(), jsonObj).get();
            return carWebSocketHandler.checkSuccess(response);
        } catch (Exception e) {
            logger.error("Failed to delete config: {}", e.getMessage());
            return false;
        }
    }
}
