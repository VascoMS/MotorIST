package sirs.motorist.prototype.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import sirs.motorist.prototype.model.entity.CarInfo;

public interface CarRepository extends MongoRepository<CarInfo, String> {
    //TODO: Maybe remove later
    CarInfo findByCarId(String carId);
}
