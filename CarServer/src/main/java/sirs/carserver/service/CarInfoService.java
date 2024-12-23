package sirs.carserver.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sirs.carserver.model.GeneralCarInfo;
import sirs.carserver.repository.CarInfoRepository;

import javax.crypto.spec.SecretKeySpec;

@Service
public class CarInfoService {

    private final CarInfoRepository carInfoRepository;
    @Value("${car.id}")
    private String carId;

    public CarInfoService(CarInfoRepository carInfoRepository ) {
        this.carInfoRepository = carInfoRepository;
    }

    public GeneralCarInfo getCarInfo(String username) {
        return carInfoRepository.findById(carId).orElse(null);
    }
}
