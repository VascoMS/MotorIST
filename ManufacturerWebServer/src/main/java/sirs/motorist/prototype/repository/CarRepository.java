package sirs.motorist.prototype.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import sirs.motorist.prototype.model.entity.CarInfo;

//TODO: REMOVE REPO later :,c
public interface CarRepository extends MongoRepository<CarInfo, String> {
    CarInfo findByCarId(String carId);
}
