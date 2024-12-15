package sirs.carserver.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Audit {
    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "config_id")
    private Config config;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
