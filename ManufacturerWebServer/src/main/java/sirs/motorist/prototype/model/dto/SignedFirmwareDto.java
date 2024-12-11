package sirs.motorist.prototype.model.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignedFirmwareDto {

    private static final Logger logger = LoggerFactory.getLogger(SignedFirmwareDto.class);

    private String manufacturerSignature;
    private int firmwareVersion;
    private String chassisNumber;

    public SignedFirmwareDto(String manufacturerSignature, int firmwareVersion, String chassisNumber) {
        this.manufacturerSignature = manufacturerSignature;
        this.firmwareVersion = firmwareVersion;
        this.chassisNumber = chassisNumber;
    }

    public String getManufacturerSignature() {
        return manufacturerSignature;
    }

    public void setManufacturerSignature(String manufacturerSignature) {
        this.manufacturerSignature = manufacturerSignature;
    }

    public int getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(int firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }
}
