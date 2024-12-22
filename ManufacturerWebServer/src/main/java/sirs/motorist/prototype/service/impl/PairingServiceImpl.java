package sirs.motorist.prototype.service.impl;

import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.util.JSONUtil;
import pt.tecnico.sirs.util.SecurityUtil;
import sirs.motorist.prototype.consts.WebSocketOpsConsts;
import sirs.motorist.prototype.model.dto.UserPairRequestDto;
import sirs.motorist.prototype.service.PairingService;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PairingServiceImpl implements PairingService {

    private final Map<String, byte[]> pairingSessions;
    private final CarWebSocketHandler carWebSocketHandler;

    PairingServiceImpl(CarWebSocketHandler carWebSocketHandler) {
        this.pairingSessions = new ConcurrentHashMap<>();
        this.carWebSocketHandler = carWebSocketHandler;
    }

    @Override
    public void initPairingSession(String carId, String code) {
        pairingSessions.put(carId, SecurityUtil.hashData(code.getBytes()));
    }

    @Override
    public boolean validatePairingSession(UserPairRequestDto request) {
        byte[] codeHash = SecurityUtil.hashData(request.getPairCode().getBytes());
        boolean verifyCodes = Arrays.equals(pairingSessions.get(request.getCarId()), codeHash);

        JsonObject jsonObj = new JsonObject();
        String nonce = JSONUtil.parseClassToJsonString(request.getNonce());
        jsonObj.addProperty(WebSocketOpsConsts.OPERATION_FIELD, "pair");
        jsonObj.addProperty(WebSocketOpsConsts.USERID_FIELD, request.getUserId());
        jsonObj.addProperty(WebSocketOpsConsts.SUCCESS_FIELD, verifyCodes);
        jsonObj.addProperty(WebSocketOpsConsts.NONCE_FIELD, nonce);

        carWebSocketHandler.sendMessageToCarNoResponse(request.getCarId(), jsonObj);

        return verifyCodes;
    }
}
