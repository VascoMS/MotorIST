package sirs.motorist.cli.sec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.*;
import java.util.Base64;

public class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    public static String signData(byte[] data, PrivateKey privateKey, byte[] nonce) throws Exception {
        logger.info("Signing data...");
        // Get a signature object
        Signature signer = Signature.getInstance("SHA256withRSA");
        // Initialize the signature object with the private key
        signer.initSign(privateKey);
        // Update the signature object with the nonce (if necessary) and data
        if(nonce != null) {
            signer.update(nonce);
        }
        signer.update(data);
        // Sign the data
        byte[] signature = signer.sign();
        // Encode the signature in Base64
        return Base64.getEncoder().encodeToString(signature);
    }
}
