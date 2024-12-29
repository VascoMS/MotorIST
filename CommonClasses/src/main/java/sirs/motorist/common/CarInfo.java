package sirs.motorist.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CarInfo implements Serializable {
    private String carId;
    private boolean isLocked;
    private int firmwareVersion;
    private int batteryLevel;
    private int totalKms;

    public CarInfo(String carId, boolean isLocked, int firmwareVersion, int batteryLevel, int totalKms) {
        this.carId = carId;
        this.isLocked = isLocked;
        this.firmwareVersion = firmwareVersion;
        this.batteryLevel = batteryLevel;
        this.totalKms = totalKms;
    }

    @Override
    public String toString() {
        return "\n\tcarId = " + carId + "\n" +
                "\tisLocked = " + isLocked + "\n" +
                "\tfirmwareVersion = " + firmwareVersion + "\n" +
                "\tbatteryLevel = " + batteryLevel + "\n" +
                "\ttotalKms = " + totalKms;
    }
}
