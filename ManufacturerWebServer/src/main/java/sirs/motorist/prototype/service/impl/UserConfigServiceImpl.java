package sirs.motorist.prototype.service.impl;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.util.JSONUtil;
import sirs.motorist.prototype.consts.WebSocketOpsConsts;
import sirs.motorist.prototype.model.dto.ConfigurationIdRequestDto;
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
    public Boolean updateConfiguration(Configuration request) {
        JsonObject jsonObj = new JsonObject();
        String nonce = JSONUtil.parseClassToJsonString(request.getNonce());
        jsonObj.addProperty(WebSocketOpsConsts.OPERATION_FIELD, "updateconfig");
        jsonObj.addProperty(WebSocketOpsConsts.USERID_FIELD, request.getUserId());
        jsonObj.addProperty(WebSocketOpsConsts.CONFIGURATION_FIELD, request.getConfiguration());
        jsonObj.addProperty(WebSocketOpsConsts.IV_FIELD, request.getIv());
        jsonObj.addProperty(WebSocketOpsConsts.NONCE_FIELD, nonce);
        jsonObj.addProperty(WebSocketOpsConsts.HMAC_FIELD, request.getHmac());
        try {
            boolean success = carWebSocketHandler.sendMessageToCarWithResponse(request.getCarId(), jsonObj).get();
            if(success) {
                configRepository.save(request);
            }
            return success;
        } catch (Exception e) {
            logger.error("Failed to update config: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean deleteConfiguration(ConfigurationIdRequestDto request) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(WebSocketOpsConsts.OPERATION_FIELD, "deleteconfig");
        jsonObj.addProperty(WebSocketOpsConsts.USERID_FIELD, request.getUserId());
        try {
            return carWebSocketHandler.sendMessageToCarWithResponse(request.getCarId(), jsonObj).get();
        } catch (Exception e) {
            logger.error("Failed to delete config: {}", e.getMessage());
            return false;
        }
    }
}
