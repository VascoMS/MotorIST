package com.sirs.carserver.repository;

import com.sirs.carserver.model.entity.Car;
import com.sirs.carserver.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, String> {
    // TODO: Add queries if needed
}
