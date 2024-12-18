package sirs.motorist.prototype.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "cars")
public class CarInfo {

    private String carId;
    private String firmwareVersion;
    private int batteryLevel;
    private String totalKms;

    public CarInfo(String carId, String firmwareVersion, int batteryLevel, String totalKms) {
        this.carId = carId;
        this.firmwareVersion = firmwareVersion;
        this.batteryLevel = batteryLevel;
        this.totalKms = totalKms;
    }
}
