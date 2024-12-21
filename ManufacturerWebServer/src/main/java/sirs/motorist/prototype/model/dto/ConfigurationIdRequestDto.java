package sirs.motorist.prototype.model.dto;

import lombok.Getter;
import lombok.Setter;
import pt.tecnico.sirs.model.Nonce;

@Getter
@Setter
public class ConfigurationIdRequestDto {

    private String userId;
    private String carId;
    private Nonce nonce;

    public ConfigurationIdRequestDto(String userId, String carId) {
        this.userId = userId;
        this.carId = carId;
    }
}
