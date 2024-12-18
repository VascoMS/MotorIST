package sirs.carserver.model;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import lombok.Getter;

@Getter
public class PairingSession {
    private final long timestamp;
    private final String code;
    private final SecureRandom secureRandom;

    public PairingSession() {
        this.timestamp = System.currentTimeMillis();
        this.secureRandom = new SecureRandom();
        this.code = UUID.randomUUID().toString();
    }
}
