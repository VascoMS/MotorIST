package sirs.motorist.prototype.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CarRepository extends MongoRepository<String, String> { //TODO: change this
}
