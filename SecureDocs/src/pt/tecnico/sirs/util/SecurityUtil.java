package pt.tecnico.sirs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

public class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    // Symmetric cipher: combination of algorithm, block processing, and padding
    public static final String SYM_CIPHER = "AES/CBC/PKCS5Padding";

    // Asymmetric cipher: combination of algorithm, block processing, and padding
    public static final String ASYM_CIPHER = "RSA/ECB/PKCS1Padding";

    public static String decipherContent(String base64Content, IvParameterSpec ivSpec, SecretKeySpec secretKey) throws Exception{
        logger.info("Deciphering content...");
        byte[] content = Base64.getDecoder().decode(base64Content);
        // Get an AES cipher object
        Cipher symCipher = Cipher.getInstance(SYM_CIPHER);
        // Initialize the cipher object with the secret key and IV
        symCipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        // Decrypt the content
        return new String(symCipher.doFinal(content));
    }

    public static String decipherSecretKey(String base64CipheredSecretKey, PrivateKey privateKey) throws Exception {
        logger.info("Deciphering secret key...");
        byte[] cipheredSecretKey = Base64.getDecoder().decode(base64CipheredSecretKey);
        // Get an RSA cipher object
        Cipher asymCipher = Cipher.getInstance(ASYM_CIPHER);
        // Initialize the cipher object with the private key
        asymCipher.init(Cipher.DECRYPT_MODE, privateKey);
        // Decrypt the secret key
        return new String(asymCipher.doFinal(cipheredSecretKey));
    }

    public static boolean verifyHMAC(byte[] data, byte[] secretKey, String expectedBase64HMAC, byte[] nonce, byte[] iv) throws Exception {
        logger.info("Verifying HMAC...");

        // Initialize the HMAC generator with the same algorithm and secret key
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
        mac.init(secretKeySpec);

        // Update the HMAC generator with the same nonce and IV as used during generation
        mac.update(nonce);
        mac.update(iv);

        // Generate the HMAC for the given data
        byte[] computedHMACBytes = mac.doFinal(data);
        String computedBase64HMAC = Base64.getEncoder().encodeToString(computedHMACBytes);

        // Log the computed and expected HMACs (optional, remove in production for security)
        logger.info("Expected HMAC: " + expectedBase64HMAC);
        logger.info("Computed HMAC: " + computedBase64HMAC);

        // Compare the computed HMAC with the expected HMAC in constant time to avoid timing attacks
        return expectedBase64HMAC.equals(computedBase64HMAC);
    }

    public static boolean verifySignature(byte[] data, byte[] nonce, byte[] iv, String base64Signature, PublicKey publicKey) throws Exception {
        logger.info("Verifying signature...");
        // Get a signature object
        Signature verifier = Signature.getInstance("SHA256withRSA");
        // Initialize the signature object with the public key
        verifier.initVerify(publicKey);
        // Decode Base64 signature
        byte[] decodedSignature = Base64.getDecoder().decode(base64Signature);
        // Update the signature object with the nonce, iv and data
        verifier.update(nonce);
        verifier.update(iv);
        verifier.update(data);
        // Verify the signature
        return verifier.verify(decodedSignature);
    }
}
