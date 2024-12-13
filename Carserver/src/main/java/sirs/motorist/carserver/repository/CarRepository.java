package sirs.motorist.carserver.repository;

import sirs.motorist.carserver.model.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, String> {
    // TODO: Add queries if needed
}
