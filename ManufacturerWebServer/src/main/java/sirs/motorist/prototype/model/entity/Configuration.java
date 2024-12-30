package sirs.motorist.prototype.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pt.tecnico.sirs.model.Nonce;

@Getter
@Setter
@Document(collection = "configs")
public class Configuration {
    @Id
    private ObjectId id;
    private String userId;
    private String carId;
    private String content;
    private String iv;
    private Nonce nonce;
    private String hmac;

    public Configuration() {
    }

    public Configuration(String userId, String carId, String content, String iv, Nonce nonce, String hmac) {
        this.userId = userId;
        this.carId = carId;
        this.content = content;
        this.iv = iv;
        this.nonce = nonce;
        this.hmac = hmac;
    }
    public Configuration(ObjectId id, String userId, String carId, String content, String iv, Nonce nonce, String hmac) {
        this.id = id;
        this.userId = userId;
        this.carId = carId;
        this.content = content;
        this.iv = iv;
        this.nonce = nonce;
        this.hmac = hmac;
    }
}
