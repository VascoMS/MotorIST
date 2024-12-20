package sirs.motorist.prototype.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import sirs.motorist.prototype.model.entity.Configuration;

public interface ConfigRepository extends MongoRepository<Configuration, String> {
    @Query("{ 'userId': ?0, 'carId': ?1 }")
    Configuration findByUserIdAndCarId(String userId, String carId);
}
