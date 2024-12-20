package sirs.motorist.prototype.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import sirs.motorist.prototype.model.entity.User;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUserId(String userId);
}
