package sirs.motorist.prototype.service.impl;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.util.JSONUtil;
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
        carWebSocketHandler.sendCommandToCar(request.getCarId(), jsonObj);

        //TODO: Add a wait mechanism for the car answers

        configRepository.save(request);
        return true;
    }

    @Override
    public Boolean deleteConfiguration(ConfigurationIdRequestDto request) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("operation", "deleteconfig");
        jsonObj.addProperty("userId", request.getUserId());
        carWebSocketHandler.sendCommandToCar(request.getCarId(), jsonObj);

        //TODO: Add a wait mechanism

        return true;
    }
}
