package sirs.motorist.cli.model.dto;

import lombok.Getter;
import lombok.Setter;
import pt.tecnico.sirs.model.Nonce;

@Setter
@Getter
public class FirmwareRequestDto {

    private String userId;
    private String password;
    private String mechanicSignature;
    private Nonce nonce;
    private String chassisNumber;

    public FirmwareRequestDto(String userId, String password, String mechanicSignature, Nonce nonce, String chassisNumber) {
        this.userId = userId;
        this.password = password;
        this.mechanicSignature = mechanicSignature;
        this.nonce = nonce;
        this.chassisNumber = chassisNumber;
    }
}
