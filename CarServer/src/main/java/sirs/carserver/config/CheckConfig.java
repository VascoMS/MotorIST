package sirs.carserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.tecnico.sirs.secdoc.Check;

@Configuration
public class CheckConfig {
    @Bean
    public Check check() {
        return new Check();
    }
}
