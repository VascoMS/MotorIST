package sirs.motorist.prototype.model.dto;

import lombok.Getter;
import lombok.Setter;
import pt.tecnico.sirs.model.Nonce;

@Getter
@Setter
public class ConfigurationRequestDto {

    private String userId;
    private String userSignature;
    private Nonce nonce;
    private String chassisNumber;

    public ConfigurationRequestDto(String userId, String userSignature, Nonce nonce, String chassisNumber) {
        this.userId = userId;
        this.userSignature = userSignature;
        this.nonce = nonce;
        this.chassisNumber = chassisNumber;
    }
}
