package sirs.motorist.cli.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigurationIdRequestDto {

    private String userId;
    private String carId;

    public ConfigurationIdRequestDto(String userId, String carId) {
        this.userId = userId;
        this.carId = carId;
    }
}
