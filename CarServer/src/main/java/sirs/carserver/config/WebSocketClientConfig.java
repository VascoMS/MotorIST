package sirs.carserver.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import sirs.carserver.service.MessageProcessorService;
import sirs.carserver.service.UserService;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.net.URI;
import java.security.KeyStore;

@Configuration
public class WebSocketClientConfig {
    @Value("${websocket.server.uri}")
    private String serverUri;

    @Value("${truststore.path}")
    private Resource truststore;

    @Value("${cert.path}")
    private Resource cert;

    @Value("${keystore.password}")
    private String keystorePassword;

    @Value("${truststore.password}")
    private String truststorePassword;


    private final MessageProcessorService messageProcessorService;

    private static final Logger logger = LoggerFactory.getLogger(WebSocketClientConfig.class);

    public WebSocketClientConfig(MessageProcessorService messageProcessorService) {
        this.messageProcessorService = messageProcessorService;
    }

    @Bean
    public CarWebSocketClient carWebSocketClient() throws Exception {
        try {
            // Load client keystore (contains client certificate and private key)
            KeyStore clientKeyStore = KeyStore.getInstance("PKCS12");
            clientKeyStore.load(cert.getInputStream(), keystorePassword.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, keystorePassword.toCharArray());

            // Load truststore (to verify server's certificate)
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            trustStore.load(truststore.getInputStream(), truststorePassword.toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            // Create SSLContext with client certs and truststore
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            // Create WebSocket client with mTLS
            CarWebSocketClient client = new CarWebSocketClient(new URI(serverUri), messageProcessorService, sslContext);
            client.connect();
            return client;
        } catch (Exception e) {
            logger.error("Error initializing SSLContext or WebSocket client: {}", e.getMessage(), e);
            throw e; // or handle more gracefully depending on your needs
        }
    }
}
