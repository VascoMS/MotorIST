package sirs.motorist.prototype.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import sirs.motorist.prototype.model.entity.Configuration;

public interface CarRepository extends MongoRepository<Configuration, String> { // TODO: CHANGE THIS
}
