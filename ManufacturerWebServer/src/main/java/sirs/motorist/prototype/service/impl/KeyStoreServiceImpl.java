package sirs.motorist.prototype.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import sirs.motorist.prototype.service.KeyStoreService;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;

@Service
public class KeyStoreServiceImpl implements KeyStoreService {

    private static final Logger logger = LoggerFactory.getLogger(KeyStoreServiceImpl.class);

    private final KeyStore keyStore;

    private final String keystorePassword;

    public KeyStoreServiceImpl(@Value("classpath:keystore.jks") Resource keystoreResource,
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

    @Override
    public PrivateKey getPrivateKey(String alias) throws Exception {
        logger.info("Getting private key from keystore...");
        return (PrivateKey) keyStore.getKey(alias, keystorePassword.toCharArray());
    }
}
