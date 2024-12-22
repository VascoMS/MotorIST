package sirs.motorist.prototype.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import sirs.motorist.prototype.service.impl.CarWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CarWebSocketHandler carWebSocketHandler;

    public WebSocketConfig(CarWebSocketHandler carWebSocketHandler) {
        this.carWebSocketHandler = carWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(carWebSocketHandler, "/car-tunnel")
                .setAllowedOrigins("*");
    }
}
