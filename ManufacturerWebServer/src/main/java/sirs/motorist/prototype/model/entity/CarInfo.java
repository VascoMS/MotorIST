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

    public CarInfo(String carId, String firmwareVersion) {
        this.carId = carId;
        this.firmwareVersion = firmwareVersion;
    }
}
