package sirs.carserver.repository;

import sirs.carserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    //TODO add stuff
    User findByUsername(String username);
}
