package sirs.carserver.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pt.tecnico.sirs.model.Nonce;

@Entity
@Getter
@Setter
public class User {
    @Id
    private String username;
    private String config;

    // Fields to decrypt the config
    private String iv;

    public User() {
    }

    public User(String username, String config, String iv) {
        this.username = username;
        this.config = config;
        this.iv = iv;
    }
}
