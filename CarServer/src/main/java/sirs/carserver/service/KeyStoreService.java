package sirs.carserver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import pt.tecnico.sirs.util.SecurityUtil;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;

@Service
public class KeyStoreService {

    private static final Logger logger = LoggerFactory.getLogger(KeyStoreService.class);

    private final KeyStore keyStore;

    private final String keystorePassword;

    private final String keystorePath;

    public KeyStoreService(@Value("${keystore.password}") String keystorePassword, @Value("${keystore.path}") String keystorePath) throws Exception {
        // Load the keystore securely
        this.keystorePassword = keystorePassword;
        this.keyStore = SecurityUtil.loadKeyStore(keystorePassword, keystorePath, "JCEKS");
        this.keystorePath = keystorePath;
    }

    public PrivateKey getPrivateKey(String alias) throws Exception {
        logger.info("Getting private key from keystore...");
        return (PrivateKey) keyStore.getKey(alias, keystorePassword.toCharArray());
    }

    public SecretKeySpec getSecretKeySpec(String alias){
        logger.info("Getting secret key from keystore...");
        // Get the key from the keystore
        try {
            Key key = keyStore.getKey(alias, keystorePassword.toCharArray());
            return new SecretKeySpec(key.getEncoded(), key.getAlgorithm());
        } catch (Exception e) {
            logger.error("Failed to get secret key from keystore.", e);
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
}

