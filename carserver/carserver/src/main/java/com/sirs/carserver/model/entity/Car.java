package com.sirs.carserver.model.entity;

import com.sirs.carserver.model.Firmware;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Car {
    @Id
    private int id;
    private String configs;

    @ManyToOne
    private Firmware firmware;
}
