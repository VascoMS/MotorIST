package pt.tecnico.sirs.secdoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.tecnico.sirs.model.Nonce;
import pt.tecnico.sirs.model.ProtectedObject;
import pt.tecnico.sirs.util.SecurityUtil;

import java.security.PublicKey;
import java.util.*;

public class Check {

    private static final Logger logger = LoggerFactory.getLogger(Check.class);

    private static final long MAX_TIMEDELTA = 30 * 1000; // 30s

    Map<String, Long> nonces = new HashMap<>();

    public Check() {}

    public boolean check(ProtectedObject protectedObject, PublicKey pubkey) {

        // Extract the content and signature from the json object
        String contentBase64 = protectedObject.getContent();
        // Turned it into a Nonce
        Nonce nonce = protectedObject.getNonce();
        byte[] iv =  Base64.getDecoder().decode(protectedObject.getIv());
        String signature = protectedObject.getSignature();
        byte[] content = Base64.getDecoder().decode(contentBase64);

        boolean signatureValid;
        try {
            signatureValid = SecurityUtil.verifySignature(
                    content,
                    signature,
                    pubkey,
                    SecurityUtil.serializeToByteArray(nonce),
                    iv
            );
        } catch (Exception e) {
            logger.error("Failed to verify hmac: {}", e.getMessage());
            return false;
        }
        nonceCleanup();

        boolean nonceValid = verifyNonce(nonce);
        boolean isValid = signatureValid && nonceValid;
        if (isValid) {
            logger.info("Everything adds up! :D");
        } else {
            logger.info("Some verification failed :c\n  - HMAC: {}\n  - Nonce: {}", signatureValid, nonceValid);
        }

        return isValid;
    }

    private boolean verifyNonce(Nonce nonce) {
        logger.info("Verifying nonce...");
        long timeDelta = System.currentTimeMillis() - nonce.timestamp();
        if (timeDelta > MAX_TIMEDELTA) {
            return false;
        }
        return !nonces.containsKey(nonce.base64Random());
    }

    private synchronized void nonceCleanup() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> iterator = nonces.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            long timestamp = entry.getValue();
            // If the nonce is older than 30 seconds, remove it
            if (currentTime - timestamp > MAX_TIMEDELTA) {
                iterator.remove();
            }
        }
    }
}
