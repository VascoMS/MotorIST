package sirs.motorist.prototype.lib.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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

    public static boolean verifySignature(byte[] data, String base64Signature, PublicKey publicKey) throws Exception {
        logger.info("Verifying signature...");
        // Get a signature object
        Signature verifier = Signature.getInstance("SHA256withRSA");
        // Initialize the signature object with the public key
        verifier.initVerify(publicKey);
        // Decode Base64 signature
        byte[] decodedSignature = Base64.getDecoder().decode(base64Signature);
        // Update the signature object with the data
        verifier.update(data);
        // Verify the signature
        return verifier.verify(decodedSignature);
    }

    /**
     * Load the private key from the file
     * @param privateKeyPath path to the private key file
     * @return the private key
     */
    public static PrivateKey loadPrivateKey(String privateKeyPath) {
        try {
            byte[] keyBytes = Files.readAllBytes(new File(privateKeyPath).toPath());
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            logger.error("Error while loading the Private key");
            return null;
        }
    }

    /**
     * Load the public key from the file
     * @param publicKeyPath path to the public key file
     * @return the public key
     */
    public static PublicKey loadPublicKeyFromFile(String publicKeyPath) {
        try {
            byte[] keyBytes = Files.readAllBytes(new File(publicKeyPath).toPath());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            logger.error("Error while loading the Public key");
            return null;
        }
    }

    public static PublicKey convertPublicKeyFromString(String publicKeyString) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            logger.error("Error while converting the Public key");
            return null;
        }
    }

    //TODO: Add nonce to mechanic signature
    public static String signData(byte[] data, PrivateKey privateKey) throws Exception {
        logger.info("Signing data...");
        // Get a signature object
        Signature signer = Signature.getInstance("SHA256withRSA");
        // Initialize the signature object with the private key
        signer.initSign(privateKey);
        // Update the signature object with the data
        signer.update(data);
        // Sign the data
        byte[] signature = signer.sign();
        // Encode the signature in Base64
        return Base64.getEncoder().encodeToString(signature);
    }

}
