package sirs.carserver.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class GeneralCarInfo {
    @Id
    private Long id = 1L; // Default ID for singleton


    private String firmwareVersion;
    private int batteryLevel;

    private String chassisNumber;
}
