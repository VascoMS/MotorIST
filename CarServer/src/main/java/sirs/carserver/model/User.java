package sirs.carserver.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
    @Id
    private String username;
    private int kmDriven;

    @OneToOne
    @JoinColumn(name = "config_id")
    private Config config;
}
