package sirs.carserver.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class WebSocketClientConfig {
    @Value("${websocket.server.uri}")
    private String serverUri;

    @Bean
    public CarWebSocketClient carWebSocketClient() throws Exception {
        CarWebSocketClient client = new CarWebSocketClient(new URI(serverUri));
        client.connect();
        return client;
    }
}
