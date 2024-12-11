package sirs.motorist.prototype.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import sirs.motorist.prototype.model.entity.Mechanic;

public interface MechanicRepository extends MongoRepository<Mechanic, String> {
}
