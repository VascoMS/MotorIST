package sirs.motorist.prototype.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;


@Setter
@Getter
@Document(collection = "users")
public class User {

    @Id
    private String userId;
    private String password;
    private String publicKey;


    public User(String userId, String publicKey, String password) {
        this.userId = userId;
        this.password = password;
        this.publicKey = publicKey;
    }

    public boolean isMechanic(){
        // Only mechanics have a public key
        return Objects.nonNull(publicKey);
    }
}

