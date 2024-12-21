package sirs.motorist.prototype.service;

import sirs.motorist.prototype.model.dto.UserPairRequestDto;

public interface PairingService {
    void initPairingSession(String carId, String code);
    boolean validatePairingSession(UserPairRequestDto request);
}
