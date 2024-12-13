package sirs.motorist.carserver.lib.secdoc;

import sirs.motorist.carserver.lib.model.Nonce;
import sirs.motorist.carserver.lib.model.ProtectedObject;
import sirs.motorist.carserver.lib.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import java.util.*;

public class Check {

    private static final Logger logger = LoggerFactory.getLogger(Check.class);

    private static final long MAX_TIMEDELTA = 30 * 1000; // 30s

    Map<String, Long> nonces = new HashMap<>();

    public Check() {}

    public boolean check(ProtectedObject protectedObject, SecretKeySpec secretKey) {

        // Extract the content and signature from the json object
        String content = protectedObject.getContent();
        // Turned it into a Nonce
        Nonce nonce = protectedObject.getNonce();
        byte[] iv =  Base64.getDecoder().decode(protectedObject.getIv());
        String expectedHmac = protectedObject.getHmac();

        boolean hmacValid;
        try {
            hmacValid = SecurityUtil.verifyHMAC(content.getBytes(), secretKey.getEncoded(), expectedHmac, SecurityUtil.serializeToByteArray(nonce), iv);
        } catch (Exception e) {
            logger.error("Failed to verify hmac: {}", e.getMessage());
            return false;
        }

        nonceCleanup();

        boolean nonceValid = verifyNonce(nonce);
        boolean isValid = hmacValid && nonceValid;


        if (isValid) {
            logger.info("Everything adds up! :D");
        } else {
            logger.info("Some verification failed :c\n  - HMAC: {}\n  - Nonce: {}", hmacValid, nonceValid);
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
