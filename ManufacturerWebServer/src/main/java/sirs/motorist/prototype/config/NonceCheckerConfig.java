package sirs.motorist.prototype.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.tecnico.sirs.secdoc.Check;

@Configuration
public class NonceCheckerConfig {
    @Bean
    public Check check() {
        return new Check();
    }
}
