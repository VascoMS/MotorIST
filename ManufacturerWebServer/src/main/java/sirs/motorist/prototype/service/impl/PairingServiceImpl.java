package sirs.motorist.prototype.service.impl;

import com.google.gson.JsonObject;
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
        boolean userExists = configRepository.existsById(request.getUserId());
        boolean verifyCodes = Arrays.equals(userCodeHash, hashedCode);
        boolean pairSuccessful = !userExists && verifyCodes;

        String base64hashedCode = Base64.getEncoder().encodeToString(hashedCode);
        JsonObject jsonObj = new JsonObject();
        String nonce = JSONUtil.parseClassToJsonString(request.getNonce());
        jsonObj.addProperty(WebSocketOpsConsts.OPERATION_FIELD, WebSocketOpsConsts.PAIR_FIELD);
        jsonObj.addProperty(WebSocketOpsConsts.USERID_FIELD, request.getUserId());
        jsonObj.addProperty(WebSocketOpsConsts.CODE_FIELD, base64hashedCode);
        jsonObj.addProperty(WebSocketOpsConsts.SUCCESS_FIELD, pairSuccessful);
        jsonObj.addProperty(WebSocketOpsConsts.NONCE_FIELD, nonce);

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
