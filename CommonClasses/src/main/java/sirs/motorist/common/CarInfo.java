package sirs.motorist.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CarInfo implements Serializable {
    private boolean isLocked;
    private int firmwareVersion;
    private int batteryLevel;
    private int totalKms;
}
