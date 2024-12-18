package sirs.carserver.model;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PairingSession {
    private final String id;
    private final long timestamp;
    private final String code;
    private static final String ALPHANUMERIC_CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 6;
    private final SecureRandom secureRandom;

    public PairingSession() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.secureRandom = new SecureRandom();
        this.code = generatePairingCode();
    }

    private String generatePairingCode() {
        StringBuilder pairingCode = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(ALPHANUMERIC_CHARACTERS.length());
            pairingCode.append(ALPHANUMERIC_CHARACTERS.charAt(randomIndex));
        }

        return pairingCode.toString();
    }

    public String toJson() {
        Map<String, Object> jsonMap = Map.of(
                "id", this.id,
                "code", this.code
        );
        Gson gson = new Gson();
        return gson.toJson(jsonMap);
    }

}
