package sirs.carserver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;

@Service
public class KeyStoreService {

    private static final Logger logger = LoggerFactory.getLogger(KeyStoreService.class);

    private final KeyStore keyStore;

    private final String keystorePassword;

    public KeyStoreService(@Value("classpath:keystore.jks") Resource keystoreResource,
                               @Value("${keystore.password}") String keystorePassword) throws Exception {
        // Load the keystore securely
        this.keystorePassword = keystorePassword;
        this.keyStore = loadKeyStore(keystoreResource, keystorePassword);
    }

    private KeyStore loadKeyStore(Resource keystoreResource, String keystorePassword) throws Exception {
        logger.info("Loading keystore...");
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (InputStream is = keystoreResource.getInputStream()) {
            keyStore.load(is, keystorePassword.toCharArray());
        }
        return keyStore;
    }

    public PrivateKey getPrivateKey(String alias) throws Exception {
        logger.info("Getting private key from keystore...");
        return (PrivateKey) keyStore.getKey(alias, keystorePassword.toCharArray());
    }
}

