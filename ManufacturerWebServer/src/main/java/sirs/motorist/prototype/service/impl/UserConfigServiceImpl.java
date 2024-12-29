package sirs.motorist.prototype.service.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.model.Nonce;
import sirs.motorist.prototype.consts.WebSocketOpsConsts;
import sirs.motorist.prototype.model.dto.WriteOperationDto;
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
    public Boolean updateConfiguration(WriteOperationDto request) {
        // TODO: We should consider passing the JsonObject building of the ProtectedObject into the class
        JsonObject jsonObj = new JsonObject();
        JsonElement nonce = request.getNonce().toJsonObject();
        jsonObj.addProperty(WebSocketOpsConsts.OPERATION_FIELD, WebSocketOpsConsts.UPDATECONFIG_OP);
        jsonObj.addProperty(WebSocketOpsConsts.USERID_FIELD, request.getUserId());
        jsonObj.addProperty(WebSocketOpsConsts.CONTENT_FIELD, request.getContent());
        jsonObj.addProperty(WebSocketOpsConsts.IV_FIELD, request.getIv());
        jsonObj.add(WebSocketOpsConsts.NONCE_FIELD, nonce);
        jsonObj.addProperty(WebSocketOpsConsts.HMAC_FIELD, request.getHmac());
        try {
            JsonObject response = carWebSocketHandler.sendMessageToCarWithResponse(request.getCarId(), jsonObj).get();
            boolean success = carWebSocketHandler.checkSuccess(response);
            if(success) {
                Configuration config = new Configuration(
                        request.getUserId(),
                        request.getCarId(),
                        request.getContent(),
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

    public Boolean updateConfigurationLocal(String carId, String userId, String configuration, String iv, Nonce nonce, String hmac){
        Configuration config = new Configuration(userId, carId, configuration, iv, nonce, hmac);
        configRepository.save(config);
        return true;
    }

    @Override
    public Boolean deleteConfiguration(WriteOperationDto request) {
        JsonObject jsonObj = new JsonObject();
        JsonElement nonce = request.getNonce().toJsonObject();
        jsonObj.addProperty(WebSocketOpsConsts.OPERATION_FIELD, WebSocketOpsConsts.DELETECONFIG_OP);
        jsonObj.addProperty(WebSocketOpsConsts.USERID_FIELD, request.getUserId());
        jsonObj.addProperty(WebSocketOpsConsts.CONTENT_FIELD, request.getContent());
        jsonObj.addProperty(WebSocketOpsConsts.IV_FIELD, request.getIv());
        jsonObj.add(WebSocketOpsConsts.NONCE_FIELD, nonce);
        jsonObj.addProperty(WebSocketOpsConsts.HMAC_FIELD, request.getHmac());
        try {
            JsonObject response = carWebSocketHandler.sendMessageToCarWithResponse(request.getCarId(), jsonObj).get();
            boolean success = carWebSocketHandler.checkSuccess(response);

            if(success) {
                configRepository.deleteConfigurationByUserIdAndCarId(request.getUserId(), request.getCarId());
            }
            return success;
        } catch (Exception e) {
            logger.error("Failed to delete config: {}", e.getMessage());
            return false;
        }
    }
}
