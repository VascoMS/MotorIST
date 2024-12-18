package sirs.carserver.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sirs.carserver.service.MessageProcessorService;

import java.net.URI;

@Configuration
public class WebSocketClientConfig {
    @Value("${websocket.server.uri}")
    private String serverUri;

    private final MessageProcessorService messageProcessorService;

    public WebSocketClientConfig(MessageProcessorService messageProcessorService) {
        this.messageProcessorService = messageProcessorService;
    }

    @Bean
    public CarWebSocketClient carWebSocketClient() throws Exception {
        CarWebSocketClient client = new CarWebSocketClient(new URI(serverUri), messageProcessorService);
        client.connect();
        return client;
    }
}
