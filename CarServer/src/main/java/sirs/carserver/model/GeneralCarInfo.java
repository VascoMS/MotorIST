package sirs.carserver.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
public class GeneralCarInfo implements Serializable {
    @Id
    private String id;
    private boolean isLocked;
    private String firmwareVersion;
    private int batteryLevel;
    private int totalKms;
}
