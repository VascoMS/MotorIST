package sirs.motorist.prototype.lib.secdoc;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sirs.motorist.prototype.lib.model.Nonce;
import sirs.motorist.prototype.lib.secure.KeyManager;
import sirs.motorist.prototype.lib.secure.ProtectedObjectBuilder;
import sirs.motorist.prototype.lib.util.FileUtil;
import sirs.motorist.prototype.lib.util.JSONUtil;
import sirs.motorist.prototype.lib.util.SecurityUtil;

import javax.crypto.spec.SecretKeySpec;
import java.util.*;

public class Check {

    private static final Logger logger = LoggerFactory.getLogger(Check.class);

    private static final long MAX_TIMEDELTA = 30 * 1000; // 30s

    Map<String, Long> nonces = new HashMap<>();

    public Check() {}

    public void check(String[] args) {
        // Check arguments
        if (args.length < 2) {
            logger.error("Argument(s) missing!");
            logger.error("Usage: java %s input_file secret_key%n  {}", Check.class.getName());
            return;
        }

        final String inputFilePath = args[0];
        final String secretKeyPath = args[1];

        final KeyManager keyManager = new KeyManager();
        final SecretKeySpec secretKey = keyManager.loadSecretKey(secretKeyPath);

        // Load the json object
        String fileContent = FileUtil.readContent(inputFilePath);
        JsonObject jsonObject = JSONUtil.parseJson(fileContent);

        // Extract the content and signature from the json object
        String content = jsonObject.get(ProtectedObjectBuilder.CONTENT_PROPERTY).getAsString();
        // Turned it into a Nonce
        Nonce nonce = JSONUtil.parseJsonToClass(jsonObject.get(ProtectedObjectBuilder.NONCE).getAsJsonObject(), Nonce.class);
        byte[] iv = getAndDecodeIv(jsonObject);
        String expectedHmac = jsonObject.get(ProtectedObjectBuilder.HMAC).getAsString();

        boolean hmacValid;
        try {
            hmacValid = SecurityUtil.verifyHMAC(content.getBytes(), secretKey.getEncoded(), expectedHmac, nonce.toByteArray(), iv);
        } catch (Exception e) {
            logger.error("Failed to verify hmac for file: {}", e.getMessage());
            return;
        }

        nonceCleanup();

        boolean nonceValid = verifyNonce(nonce);
        boolean isValid = hmacValid && nonceValid;


        if (isValid) {
            logger.info("Everything adds up! :D");
        } else {
            logger.info("Some verification failed :c\n  - HMAC: {}\n  - Nonce: {}", hmacValid, nonceValid);
        }
    }

    private byte[] getAndDecodeIv(JsonObject jsonObject) {
        String iv = jsonObject.get(ProtectedObjectBuilder.IV_PROPERTY).getAsString();
        return Base64.getDecoder().decode(iv);
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
