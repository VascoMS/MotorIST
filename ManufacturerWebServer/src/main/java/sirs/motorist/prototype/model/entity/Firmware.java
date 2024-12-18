package sirs.motorist.prototype.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "firmwares")
public class Firmware {

    @Id
    private int version;
    private String description;

    public Firmware(int version, String description) {
        this.version = version;
        this.description = description;
    }

    public byte[] getAsByteArray() {
        return (version + description).getBytes();
    }
}
