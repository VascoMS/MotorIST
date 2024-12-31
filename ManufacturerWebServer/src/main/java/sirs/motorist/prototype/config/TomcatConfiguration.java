package sirs.motorist.prototype.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfiguration {
    @Value("${server.ssl.key-store-password}")
    private String keyStorePassword;

    @Value("${server.ssl.key-store-type}")
    private String keyStoreType;

    @Value("${server.ssl.key-store}")
    private String keyStorePath;

    @Value("${trust-store}")
    private String trustStorePath;

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerFactoryCustomizer() {
        return factory -> {
            factory.addAdditionalTomcatConnectors(createWebSocketConnector());
        };
    }

    private Connector createWebSocketConnector() {
        Connector connector = new Connector(Http11NioProtocol.class.getName());
        connector.setScheme("https");
        connector.setPort(444);
        connector.setSecure(true);

        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();

        // Create SSLHostConfig
        SSLHostConfig sslHostConfig = new SSLHostConfig();
        sslHostConfig.setHostName("_default_");

        // Configure SSL certificate
        SSLHostConfigCertificate cert = new SSLHostConfigCertificate(sslHostConfig, SSLHostConfigCertificate.Type.UNDEFINED);
        cert.setCertificateKeystoreFile(keyStorePath);
        cert.setCertificateKeystorePassword(keyStorePassword);
        cert.setCertificateKeystoreType(keyStoreType);


        // Configure trust store
        sslHostConfig.setTruststoreFile(trustStorePath);
        sslHostConfig.setTruststorePassword(keyStorePassword);
        sslHostConfig.setTruststoreType(keyStoreType);

        // Enable client authentication for mutual TLS
        sslHostConfig.setCertificateVerification("required");

        sslHostConfig.addCertificate(cert);

        // Add the SSL config to connector
        connector.addSslHostConfig(sslHostConfig);

        protocol.setSSLEnabled(true);

        return connector;
    }
}
