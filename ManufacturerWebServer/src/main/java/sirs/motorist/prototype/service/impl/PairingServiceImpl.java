package sirs.motorist.prototype.service.impl;

import pt.tecnico.sirs.util.SecurityUtil;
import sirs.motorist.prototype.service.PairingService;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PairingServiceImpl implements PairingService {

    private final Map<String, byte[]> pairingSessions;

    PairingServiceImpl() {
        this.pairingSessions = new ConcurrentHashMap<>();
    }

    @Override
    public void initPairingSession(String carId, String code) {
        pairingSessions.put(carId, SecurityUtil.hashData(code.getBytes()));
    }


    @Override
    public boolean validatePairingSession(String carId, String code) {
        byte[] codeHash = SecurityUtil.hashData(code.getBytes());
        return Arrays.equals(pairingSessions.get(carId), codeHash);
    }
}
