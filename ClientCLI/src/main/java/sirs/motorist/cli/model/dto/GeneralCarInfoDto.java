package sirs.motorist.cli.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GeneralCarInfoDto implements Serializable {

    private boolean isLocked;
    private int firmwareVersion;
    private int batteryLevel;
    private int totalKms;

    @Override
    public String toString() {
        return "GeneralCarInfoDto:\n" +
                "\tisLocked = " + isLocked + "\n" +
                "\tfirmwareVersion = " + firmwareVersion + "\n" +
                "\tbatteryLevel = " + batteryLevel + "\n" +
                "\ttotalKms = " + totalKms + "\n";
    }
}
