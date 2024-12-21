package sirs.motorist.prototype.service.impl;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.util.JSONUtil;
import sirs.motorist.prototype.model.dto.ConfigurationIdRequestDto;
import sirs.motorist.prototype.model.dto.UserPairRequestDto;
import sirs.motorist.prototype.model.entity.CarInfo;
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
    public Boolean pairNewUser(UserPairRequestDto request) {
        return null;
    }

    @Override
    public Configuration getConfiguration(String userId, String carId) {
        Configuration config = configRepository.findByUserIdAndCarId(userId, carId);
        if (config == null) {
            logger.error("Configuration for that user and car was not found...");
            return null;
        }
        return config;
    }

    @Override
    public Boolean updateConfiguration(Configuration request) {
        JsonObject jsonObj = new JsonObject();
        String nonce = JSONUtil.parseClassToJsonString(request.getNonce());
        jsonObj.addProperty("operation", "updateconfig");
        jsonObj.addProperty("userId", request.getUserId());
        jsonObj.addProperty("configuration", request.getConfiguration());
        jsonObj.addProperty("iv", request.getIv());
        jsonObj.addProperty("nonce", nonce);
        jsonObj.addProperty("hmac", request.getHmac());
        try {
            boolean success = carWebSocketHandler.sendCommandToCar(request.getCarId(), jsonObj).get();
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
        jsonObj.addProperty("operation", "deleteconfig");
        jsonObj.addProperty("userId", request.getUserId());
        try {
            return carWebSocketHandler.sendCommandToCar(request.getCarId(), jsonObj).get();
        } catch (Exception e) {
            logger.error("Failed to delete config: {}", e.getMessage());
            return false;
        }
    }
}
