package sirs.motorist.prototype.service.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.model.ProtectedObject;
import pt.tecnico.sirs.util.JSONUtil;
import pt.tecnico.sirs.util.SecurityUtil;
import sirs.motorist.prototype.consts.WebSocketOpsConsts;
import sirs.motorist.prototype.model.PairingSessionRecord;
import sirs.motorist.prototype.model.dto.UserPairRequestDto;
import sirs.motorist.prototype.model.entity.Configuration;
import sirs.motorist.prototype.repository.ConfigRepository;
import sirs.motorist.prototype.service.PairingService;

import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PairingServiceImpl implements PairingService {

    private static final Logger logger = LoggerFactory.getLogger(PairingServiceImpl.class);

    private final Map<String, PairingSessionRecord> pairingSessions;
    private final CarWebSocketHandler carWebSocketHandler;
    private final ConfigRepository configRepository;

    PairingServiceImpl(CarWebSocketHandler carWebSocketHandler, ConfigRepository configRepository) {
        this.pairingSessions = new ConcurrentHashMap<>();
        this.carWebSocketHandler = carWebSocketHandler;
        this.configRepository = configRepository;
    }

    @Override
    public void initPairingSession(String carId, String code, ProtectedObject protectedConfig) {
        byte[] hashedCode = SecurityUtil.hashData(code.getBytes());
        pairingSessions.put(carId, new PairingSessionRecord(hashedCode, protectedConfig));
    }

    @Override
    public boolean validatePairingSession(UserPairRequestDto request) {
        byte[] userCodeHash = SecurityUtil.hashData(request.getPairCode().getBytes());
        PairingSessionRecord pairingSession = pairingSessions.get(request.getCarId());
        byte[] hashedCode = pairingSession.hashedCode();
        boolean userExists = configRepository.existsByUserIdAndCarId(request.getUserId(), request.getCarId());
        logger.info("User {} exists: {}", request.getUserId(), userExists);
        boolean verifyCodes = Arrays.equals(userCodeHash, hashedCode);
        boolean pairSuccessful = !userExists && verifyCodes;
        logger.info("User code matches: {}", verifyCodes);

        String base64hashedCode = Base64.getEncoder().encodeToString(hashedCode);
        JsonObject jsonObj = new JsonObject();
        JsonElement nonce = request.getNonce().toJsonObject();
        jsonObj.addProperty(WebSocketOpsConsts.OPERATION_FIELD, WebSocketOpsConsts.PAIR_OP);
        jsonObj.addProperty(WebSocketOpsConsts.USERID_FIELD, request.getUserId());
        jsonObj.addProperty(WebSocketOpsConsts.CODE_FIELD, base64hashedCode);
        jsonObj.addProperty(WebSocketOpsConsts.SUCCESS_FIELD, pairSuccessful);
        jsonObj.add(WebSocketOpsConsts.NONCE_FIELD, nonce);

        System.out.println("Sending message to car: " + jsonObj);

        carWebSocketHandler.sendMessageToCarNoResponse(request.getCarId(), jsonObj);
        if(pairSuccessful) {
            ProtectedObject protectedConfig = pairingSession.protectedConfig();
            Configuration configuration = new Configuration(
                    request.getUserId(),
                    request.getCarId(),
                    protectedConfig.getContent(),
                    protectedConfig.getIv(),
                    protectedConfig.getNonce(),
                    protectedConfig.getHmac()
            );
            configRepository.save(configuration);

            pairingSessions.remove(request.getCarId());
        }

        return pairSuccessful;
    }
}
