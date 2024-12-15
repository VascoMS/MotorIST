package sirs.motorist.prototype.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class Firmware {

    @Id
    private String id;
    private int version;

    public Firmware(String id, int version) {
        this.id = id;
        this.version = version;
    }

    public byte[] getAsByteArray() {
        return (version + "").getBytes();
    }
}
