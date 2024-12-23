package sirs.motorist.prototype.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CarInfoDto {
    private String userId;
    private String carId;
    private String carInfo;
    private String iv;
    private String hmac;
}
