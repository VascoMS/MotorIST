package com.sirs.carserver.lib.secure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

//TODO: we should generate the keys: priv and pub in a Java's KeyStore | secret is generated everytime
public class KeyManager {

    private static final Logger logger = LoggerFactory.getLogger(KeyManager.class);

    private PublicKey publicKey;
    private PrivateKey privateKey;
    private SecretKeySpec secretKey;

    public KeyManager(){}

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public SecretKeySpec getSecretKey() {
        return secretKey;
    }



    /**
     * Load the public key from the file
     * @param publicKeyPath path to the public key file
     * @return the public key
     */
    public PublicKey loadPublicKey(String publicKeyPath) {
        try {
            byte[] keyBytes = Files.readAllBytes(new File(publicKeyPath).toPath());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            logger.error("Error while loading the Public key");
            System.exit(1);
        }
        return this.publicKey;
    }

    /**
     * Load the secret key from the file
     * @param secretKeyPath path to the secret key file
     * @return the secret key
     */
    public SecretKeySpec loadSecretKey(String secretKeyPath) {
        try {
            // Read the secret key from the file
            byte[] keyBytes = Files.readAllBytes(new File(secretKeyPath).toPath());
            this.secretKey = new SecretKeySpec(keyBytes, "AES");
        } catch (IOException ex) {
            logger.error("Error while loading the Secret key");
            System.exit(1);
        }
        return this.secretKey;
    }

}
