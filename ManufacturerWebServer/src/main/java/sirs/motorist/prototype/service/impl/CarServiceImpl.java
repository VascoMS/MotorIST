package sirs.motorist.prototype.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sirs.motorist.prototype.model.dto.PairingRequestDto;
import sirs.motorist.prototype.repository.CarRepository;
import sirs.motorist.prototype.service.CarService;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public Boolean checkPairingCodes(PairingRequestDto request) {
        return null; //TODO: to implement
    }
}
