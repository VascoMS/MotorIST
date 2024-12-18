package sirs.motorist.prototype.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Setter
@Getter
@Document(collection = "users")
public class User {

    @Id
    private String userId;
    private String publicKey;
    private Boolean isMechanic;

    public User(String userId, String publicKey, Boolean isMechanic) {
        this.userId = userId;
        this.publicKey = publicKey;
        this.isMechanic = isMechanic;
    }
}

