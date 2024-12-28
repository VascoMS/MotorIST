package sirs.carserver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.tecnico.sirs.util.SecurityUtil;

import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.security.*;

@Service
public class KeyStoreService {

    private static final Logger logger = LoggerFactory.getLogger(KeyStoreService.class);

    private final KeyStore keyStore;

    private final String keystorePassword;

    private final String keystorePath;

    public KeyStoreService(@Value("${keystore.password}") String keystorePassword, @Value("${keystore.path}") String keystorePath) throws Exception {
        // Load the keystore securely
        this.keystorePassword = keystorePassword;
        this.keyStore = SecurityUtil.loadKeyStore(keystorePassword, keystorePath);
        this.keystorePath = keystorePath;
    }

    public PrivateKey getPrivateKey(String alias) throws Exception {
        logger.info("Getting private key from keystore...");
        return (PrivateKey) keyStore.getKey(alias, keystorePassword.toCharArray());
    }

    public PublicKey getPublicKey(String alias) throws Exception {
        logger.info("Getting public key from keystore...");
        return (PublicKey) keyStore.getKey(alias, keystorePassword.toCharArray());
    }

    public SecretKeySpec getSecretKeySpec(String alias){
        logger.info("Getting secret key from keystore...");
        // Get the key from the keystore
        try {
            Key key = keyStore.getKey(alias, keystorePassword.toCharArray());
            return new SecretKeySpec(key.getEncoded(), key.getAlgorithm());
        } catch (Exception e) {
            logger.error("Failed to get secret key from keystore: {}", e.getMessage());
            return null;
        }
    }

    public void storeNewKey(SecretKeySpec secretKey, String userId) throws Exception {
        logger.info("Storing new key in keystore...");

        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);

        KeyStore.PasswordProtection keyPassword = new KeyStore.PasswordProtection(keystorePassword.toCharArray());

        keyStore.setEntry(userId, secretKeyEntry, keyPassword);

        try (FileOutputStream keystoreFile = new FileOutputStream(keystorePath)) {
            keyStore.store(keystoreFile, keystorePassword.toCharArray());
            logger.info("New key stored in keystore successfully.");
        } catch (Exception e) {
            logger.error("Failed to store key in keystore.", e);
            throw e;
        }
    }

    public void deleteKey(String userId) throws Exception {
        if (keyStore.containsAlias(userId)) {
            // Delete the entry
            keyStore.deleteEntry(userId);
            logger.info("Removed key for user: {}", userId);
        } else {
            logger.error("Unable to remove key for user: {}", userId);
        }
        // Save the updated KeyStore back to the file
        try (FileOutputStream fos = new FileOutputStream(keystorePath)) {
            keyStore.store(fos, keystorePassword.toCharArray());
        }
    }
}

