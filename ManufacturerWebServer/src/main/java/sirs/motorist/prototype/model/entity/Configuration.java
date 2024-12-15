package sirs.motorist.prototype.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class Configuration {

    @Id
    private String userId;

    public Configuration(String userId) {
        this.userId = userId;
    }


}
