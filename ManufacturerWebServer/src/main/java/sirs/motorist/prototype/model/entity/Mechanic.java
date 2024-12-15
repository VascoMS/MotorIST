package sirs.motorist.prototype.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Setter
@Getter
@Document
public class Mechanic {

    @Id
    private String id;
    private String publicKey;

    public Mechanic(String id, String publicKey) {
        this.id = id;
        this.publicKey = publicKey;
    }
}

