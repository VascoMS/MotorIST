package sirs.carserver.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CarInfoDto {
    private String carId;
    private int batteryLevel;
    private int totalKms;
}
