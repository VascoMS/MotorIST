package sirs.motorist.prototype.model.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirmwareRequestDto {

    private static final Logger logger = LoggerFactory.getLogger(FirmwareRequestDto.class);

    private String mechanicId;
    private String mechanicSignature;
    private String chassisNumber;

    public FirmwareRequestDto(String mechanicId, String mechanicSignature, String chassisNumber) {
        this.mechanicId = mechanicId;
        this.mechanicSignature = mechanicSignature;
        this.chassisNumber = chassisNumber;
    }

    public String getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(String mechanicId) {
        this.mechanicId = mechanicId;
    }

    public String getMechanicSignature() {
        return mechanicSignature;
    }

    public void setMechanicSignature(String mechanicSignature) {
        this.mechanicSignature = mechanicSignature;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }
}
