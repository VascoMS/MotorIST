package sirs.motorist.prototype.service.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.util.JSONUtil;
import sirs.motorist.prototype.consts.WebSocketOpsConsts;
import sirs.motorist.prototype.model.dto.ProtectedCarInfoDto;
import sirs.motorist.prototype.model.dto.InfoGetterDto;
import sirs.motorist.prototype.model.entity.Configuration;
import sirs.motorist.prototype.repository.ConfigRepository;
import sirs.motorist.prototype.service.CarService;

@Service
public class CarServiceImpl implements CarService {
    private static final Logger logger = LoggerFactory.getLogger(CarServiceImpl.class);
    private final ConfigRepository configRepository;
    private final CarWebSocketHandler carWebSocketHandler;

    @Autowired
    public CarServiceImpl(ConfigRepository configRepository, CarWebSocketHandler carWebSocketHandler) {
        this.configRepository = configRepository;
        this.carWebSocketHandler = carWebSocketHandler;
    }

    @Override
    public ProtectedCarInfoDto getCarInfo(InfoGetterDto request) {
        String userId = request.getUserId();
        String carId = request.getCarId();
        Configuration config = configRepository.findByUserIdAndCarId(userId, carId);
        if (config == null) {
            logger.error("User {} isn't registered with car {}...", userId, carId);
            return null;
        }
        JsonObject jsonObj = new JsonObject();
        JsonElement nonce = request.getNonce().toJsonObject();
        jsonObj.addProperty(WebSocketOpsConsts.OPERATION_FIELD, WebSocketOpsConsts.GENERALCARINFO_OP);
        jsonObj.addProperty(WebSocketOpsConsts.USERID_FIELD, request.getUserId());
        jsonObj.add(WebSocketOpsConsts.NONCE_FIELD, nonce);
        try {

            JsonObject response = carWebSocketHandler.sendMessageToCarWithResponse(carId, jsonObj).get();
            logger.info("Received response from car {} Response: {}...", carId, response.toString());
            return JSONUtil.parseJsonToClass(response, ProtectedCarInfoDto.class);
        } catch (Exception e) {
            logger.error("Failed to get car info: {}", e.getMessage());
            return null;
        }
    }
}
