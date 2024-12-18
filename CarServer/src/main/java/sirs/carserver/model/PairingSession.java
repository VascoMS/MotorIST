package sirs.carserver.model;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import lombok.Getter;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

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
