package pt.tecnico.sirs.secure;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.tecnico.sirs.model.Nonce;
import pt.tecnico.sirs.util.SecurityUtil;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.*;
import java.util.Base64;

public class ProtectedObjectBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ProtectedObjectBuilder.class);

    // Json properties
    public static final String CONTENT_PROPERTY = "content";
    public static final String IV_PROPERTY = "iv";
    public static final String SECRET_KEY_PROPERTY = "secret_key";
    public static final String SIGNATURE = "signature";
    public static final String NONCE = "nonce";
    public static final String HMAC = "hmac";

    // IV size in bytes
    public static final int IV_SIZE = 16;

    private final JsonObject jsonObject;

    private final SecureRandom secureRandom;

    private byte[] iv;

    private byte[] nonce;

    public ProtectedObjectBuilder() {
        this.secureRandom = new SecureRandom();
        this.jsonObject = new JsonObject();
    }

    public ProtectedObjectBuilder cipherContent(byte[] content, SecretKeySpec secretKey) throws Exception {
        // Get an AES cipher object
        Cipher symCipher = Cipher.getInstance(SecurityUtil.SYM_CIPHER);

        logger.info("Generating the IV...");

        // Generate the IV
        this.iv = this.generateIv();
        IvParameterSpec ivSpec = new IvParameterSpec(this.iv);
        // Encrypt the text using the secret key
        symCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        // Cipher the content of the document

        logger.info("Ciphering the content...");

        byte[] contentCipherBytes = symCipher.doFinal(content);

        // Convert the ciphered content to a base64 string
        String base64Content = Base64.getEncoder().encodeToString(contentCipherBytes);
        // Add the ciphered content to the JSON object

        logger.info("Adding the ciphered content and IV to the JSON object...");

        jsonObject.addProperty(CONTENT_PROPERTY, base64Content);
        jsonObject.addProperty(IV_PROPERTY, Base64.getEncoder().encodeToString(iv));
        return this;
    }

    private byte[] generateIv(){
        byte[] iv = new byte[IV_SIZE];
        secureRandom.nextBytes(iv);
        return iv;
    }

    public ProtectedObjectBuilder cipherSecretKey(SecretKey secretKey, PublicKey publicKey) throws Exception {
        // Get an RSA cipher object
        Cipher asymCipher = Cipher.getInstance(SecurityUtil.ASYM_CIPHER);
        // Encrypt the secret key using the public key
        asymCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // Cipher the secret key

        logger.info("Ciphering the secret key...");

        byte[] secretKeyCipherBytes = asymCipher.doFinal(secretKey.getEncoded());
        // Convert the ciphered secret key to a base64 string
        String base64SecretKey = Base64.getEncoder().encodeToString(secretKeyCipherBytes);
        // Add the ciphered secret key to the JSON object

        logger.info("Adding the ciphered secret key to the JSON object...");

        this.jsonObject.addProperty(SECRET_KEY_PROPERTY, base64SecretKey);
        return this;
    }

    public ProtectedObjectBuilder generateHMAC(byte[] data, byte[] secretKey) throws Exception {
        logger.info("Generating HMAC...");

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
        mac.init(secretKeySpec);
        mac.update(this.nonce);
        mac.update(this.iv);
        byte[] hmacBytes = mac.doFinal(data);
        String base64hmac = Base64.getEncoder().encodeToString(hmacBytes);

        logger.info("Adding the HMAC to the JSON object...");

        jsonObject.addProperty(HMAC, base64hmac);
        return this;
    }

    public ProtectedObjectBuilder signData(byte[] data, PrivateKey privateKey) throws Exception {
        String base64Signature = SecurityUtil.signData(data, privateKey);
        logger.info("Adding the signature to the JSON object...");
        jsonObject.addProperty(SIGNATURE, base64Signature);
        return this;
    }

    public ProtectedObjectBuilder generateNonce(int lengthInBytes) throws IOException {
        logger.info("Generating nonce...");
        byte[] nonceBytes = new byte[lengthInBytes];
        this.secureRandom.nextBytes(nonceBytes);
        // Convert the nonce to a base64 string for easy transmission
        String base64Random = Base64.getUrlEncoder().withoutPadding().encodeToString(nonceBytes);
        long timestamp = System.currentTimeMillis();
        Nonce newNonce = new Nonce(base64Random, timestamp);
        jsonObject.add(NONCE, newNonce.toJsonObject());
        this.nonce = newNonce.toByteArray();
        return this;
    }

    public JsonObject build(){
        return this.jsonObject;
    }
}
