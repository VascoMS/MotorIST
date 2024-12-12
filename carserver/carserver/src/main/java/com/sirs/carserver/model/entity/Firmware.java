package com.sirs.carserver.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Firmware {
    @Id
    private String id;
    private int version;
}
