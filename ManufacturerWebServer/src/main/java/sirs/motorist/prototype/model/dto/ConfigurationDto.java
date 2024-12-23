package sirs.motorist.prototype.model.dto;

import lombok.Getter;
import lombok.Setter;
import pt.tecnico.sirs.model.Nonce;

@Getter
@Setter
public class ConfigurationDto {

    private String userId;
    private String carId;
    private String password;
    private String configuration;
    private String iv;
    private Nonce nonce;
    private String hmac;

    public ConfigurationDto(String userId, String carId, String password, String configuration, String iv, Nonce nonce, String hmac) {
        this.userId = userId;
        this.carId = carId;
        this.password = password;
        this.configuration = configuration;
        this.iv = iv;
        this.nonce = nonce;
        this.hmac = hmac;
    }
}
