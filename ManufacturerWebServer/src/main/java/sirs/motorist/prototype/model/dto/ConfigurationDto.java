package sirs.motorist.prototype.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigurationDto {

    private String userId;
    private String carId;
    private String configuration;

    public ConfigurationDto(String userId, String carId, String configuration) {
        this.userId = userId;
        this.carId = carId;
        this.configuration = configuration;
    }
}
