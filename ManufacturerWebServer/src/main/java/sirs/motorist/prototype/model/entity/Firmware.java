package sirs.motorist.prototype.model.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Firmware {

    private static final Logger logger = LoggerFactory.getLogger(Firmware.class);

    @Id
    private String id;
    private int version;

    public Firmware(String id, int version) {
        this.id = id;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public byte[] getAsByteArray() {
        return (version + "").getBytes();
    }
}
