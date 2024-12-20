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

    //TODO: Remove this shit -> its just to test if the db is creating everything properly etc.
    @Bean
    public CommandLineRunner demo(UserRepository repository) {
        return (args) -> {
            // Create and save a User
            User user = new User();
            user.setUsername("john_doe");
            user.setConfig("thisIsAConfig");
            repository.save(user);
            // Create and save a User2
            User user2 = new User();
            user.setUsername("john_2_doe");
            user.setConfig("thisIsAConfig_2");
            repository.save(user2);

            // Fetch and print the user by username
            User fetchedUser = repository.findByUsername("john_doe");
            System.out.println("Fetched User: " + fetchedUser);

            // Fetch and print all users
            System.out.println("Saved Users:");
            repository.findAll().forEach(System.out::println);
        };
    }
}

