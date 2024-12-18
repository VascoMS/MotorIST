package sirs.motorist.cli.dto;

import lombok.Getter;
import lombok.Setter;
import pt.tecnico.sirs.model.Nonce;

@Getter
@Setter
public class FirmwareRequestDto {

    private String mechanicId;
    private String mechanicSignature;
    private Nonce nonce;
    private String chassisNumber;

    public FirmwareRequestDto(String mechanicId, String mechanicSignature, Nonce nonce, String chassisNumber) {
        this.mechanicId = mechanicId;
        this.mechanicSignature = mechanicSignature;
        this.nonce = nonce;
        this.chassisNumber = chassisNumber;
    }
}
