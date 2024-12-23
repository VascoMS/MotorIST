package sirs.motorist.prototype.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sirs.motorist.prototype.model.dto.ConfigurationIdRequestDto;
import sirs.motorist.prototype.model.entity.CarInfo;
import sirs.motorist.prototype.model.entity.Configuration;
import sirs.motorist.prototype.repository.CarRepository;
import sirs.motorist.prototype.repository.ConfigRepository;
import sirs.motorist.prototype.service.CarService;

@Service
public class CarServiceImpl implements CarService {
    //TODO: Don't store
    private static final Logger logger = LoggerFactory.getLogger(CarServiceImpl.class);
    private final CarRepository carRepository;
    private final ConfigRepository configRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository, ConfigRepository configRepository) {
        this.carRepository = carRepository;
        this.configRepository = configRepository;
    }

    @Override
    public CarInfo getCarInfo(ConfigurationIdRequestDto request) {
        // TODO:
        Configuration config = configRepository.findByUserIdAndCarId(request.getUserId(), request.getCarId());
        if (config == null) {
            logger.error("Configuration for that user and car was not found...");
            return null;
        }
        return carRepository.findByCarId(request.getCarId());
    }
}
