package sirs.motorist.cli.dto;

public class FirmwareRequestDto {

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
