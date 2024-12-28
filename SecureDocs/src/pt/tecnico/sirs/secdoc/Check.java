package pt.tecnico.sirs.secdoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.tecnico.sirs.model.Nonce;
import pt.tecnico.sirs.model.ProtectedObject;
import pt.tecnico.sirs.util.SecurityUtil;

import javax.crypto.SecretKey;
import java.util.*;

public class Check {

    private static final Logger logger = LoggerFactory.getLogger(Check.class);

    private static final long MAX_TIMEDELTA = 30 * 1000; // 30s

    Map<String, Long> nonces = new HashMap<>();

    public Check() {}

    public boolean check(ProtectedObject protectedObject, SecretKey secretKey, boolean hasNonce) {

        // Extract the content and signature from the json object
        String contentBase64 = protectedObject.getContent();
        // Turned it into a Nonce
        Nonce nonce = protectedObject.getNonce();
        byte[] iv =  Base64.getDecoder().decode(protectedObject.getIv());
        String hmac = protectedObject.getHmac();
        byte[] content = Base64.getDecoder().decode(contentBase64);

        byte[] nonceBytes = null;
        if (hasNonce) {
            try {
                nonceBytes = SecurityUtil.serializeToByteArray(nonce);
            } catch (Exception e) {
                logger.error("Error serializing nonce: {}", e.getMessage());
                return false; // Optionally handle error, return false, or throw exception
            }
        }

        boolean hmacValid;
        try {
            hmacValid = SecurityUtil.verifyHMAC(
                    content,
                    secretKey.getEncoded(),
                    hmac,
                    nonceBytes,
                    iv
            );
        } catch (Exception e) {
            logger.error("Failed to verify hmac: {}", e.getMessage());
            return false;
        }

        boolean nonceValid = true;
        if (hasNonce) {
            nonceValid = verifyNonce(nonce);
        }
        boolean isValid = hmacValid && nonceValid;
        if (isValid) {
            logger.info("Everything adds up! :D");
        } else {
            logger.info("Some verification failed :c\n  - HMAC: {}\n  - Nonce: {}", hmacValid, nonceValid);
        }

        return isValid;
    }

    public boolean verifyNonce(Nonce nonce) {
        logger.info("Verifying nonce...");
        nonceCleanup();
        long timeDelta = System.currentTimeMillis() - nonce.timestamp();
        if (timeDelta > MAX_TIMEDELTA) {
            return false;
        }
        boolean valid = !nonces.containsKey(nonce.base64Random());
        nonces.put(nonce.base64Random(), nonce.timestamp());
        return valid;
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
