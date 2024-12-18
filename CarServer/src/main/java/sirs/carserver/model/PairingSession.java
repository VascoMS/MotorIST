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
    private final SecretKey secretKey;
    private static final String ALPHANUMERIC_CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 6;

    public PairingSession() {
        this.timestamp = System.currentTimeMillis();
        this.code = generatePairingCode();
        this.secretKey = generateKey();
    }

    public SecretKey generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("AES algorithm is not available", e);
        }

    }

    private String generatePairingCode() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder pairingCode = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(ALPHANUMERIC_CHARACTERS.length());
            pairingCode.append(ALPHANUMERIC_CHARACTERS.charAt(randomIndex));
        }
        return pairingCode.toString();
    }
}
