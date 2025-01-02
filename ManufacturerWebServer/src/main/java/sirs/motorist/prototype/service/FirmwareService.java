package sirs.motorist.prototype.service;

import sirs.motorist.prototype.model.dto.FirmwareRequestDto;
import sirs.motorist.prototype.model.dto.SignedFirmwareDto;

public interface FirmwareService {
    boolean checkMechanicSignature(FirmwareRequestDto firmwareDownloadRequest);
    SignedFirmwareDto fetchAndSignFirmware(String chassisNumber);
    boolean addFirmware(int version, String description);
}
