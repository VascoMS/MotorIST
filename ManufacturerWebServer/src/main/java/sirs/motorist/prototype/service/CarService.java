package sirs.motorist.prototype.service;

import sirs.motorist.prototype.model.dto.PairingRequestDto;

public interface CarService {
    Boolean checkPairingCodes(PairingRequestDto request);
}
