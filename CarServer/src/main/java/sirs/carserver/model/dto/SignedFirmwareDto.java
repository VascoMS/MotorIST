package sirs.carserver.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignedFirmwareDto {

    private String manufacturerSignature;
    private int firmwareVersion;
    private String chassisNumber;

    public SignedFirmwareDto(String manufacturerSignature, int firmwareVersion, String chassisNumber) {
        this.manufacturerSignature = manufacturerSignature;
        this.firmwareVersion = firmwareVersion;
        this.chassisNumber = chassisNumber;
    }
}
