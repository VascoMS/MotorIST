package sirs.carserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import sirs.carserver.model.User;
import sirs.carserver.repository.UserRepository;

@SpringBootApplication
public class CarApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarApplication.class, args);
    }
}

