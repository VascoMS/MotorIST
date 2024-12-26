package sirs.motorist.prototype.service;

import pt.tecnico.sirs.model.ProtectedObject;
import sirs.motorist.prototype.model.dto.UserPairRequestDto;

public interface PairingService {
    void initPairingSession(String carId, String code, ProtectedObject protectedConfig);
    boolean validatePairingSession(UserPairRequestDto request);
}
