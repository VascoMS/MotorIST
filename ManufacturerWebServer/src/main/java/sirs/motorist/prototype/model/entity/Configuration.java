package sirs.motorist.prototype.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "configs")
public class Configuration {

    @Id
    private String userId;
    private String carId;
    private String configuration;

    public Configuration(String userId, String carId, String configuration) {
        this.userId = userId;
        this.carId = carId;
        this.configuration = configuration;
    }
}
