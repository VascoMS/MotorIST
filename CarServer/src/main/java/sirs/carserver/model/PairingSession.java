package sirs.carserver.model;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

import lombok.Getter;
import pt.tecnico.sirs.util.SecurityUtil;
import sirs.motorist.common.Config;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

@Getter
public class PairingSession {
    private final long timestamp;
    private final String code;
    private final SecretKeySpec secretKey;
    private final Config defaultConfig;
    private static final String ALPHANUMERIC_CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 6;

    public PairingSession() {
        this.timestamp = System.currentTimeMillis();
        this.code = generatePairingCode();
        this.secretKey = generateKey();
        this.defaultConfig = new Config();
    }

    public SecretKeySpec generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            byte[] encodedKey = keyGen.generateKey().getEncoded();
            return new SecretKeySpec(encodedKey, "AES");
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

    public boolean validateCode(byte[] code) {
        return Arrays.equals(SecurityUtil.hashData(this.code.getBytes()), code);
    }
}
