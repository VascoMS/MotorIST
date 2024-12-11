package sirs.motorist.prototype.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import sirs.motorist.prototype.model.entity.Firmware;

public interface FirmwareRepository extends MongoRepository<Firmware, String> {
    Firmware findTopByOrderByVersionDesc();
}
