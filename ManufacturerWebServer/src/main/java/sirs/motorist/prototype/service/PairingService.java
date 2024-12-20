package sirs.motorist.prototype.service;

public interface PairingService {
    void initPairingSession(String carId, String code);
    boolean validatePairingSession(String carId, String code);
}
