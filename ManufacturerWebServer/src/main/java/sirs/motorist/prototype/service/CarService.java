package sirs.motorist.prototype.service;

import sirs.motorist.prototype.model.dto.ConfigurationIdRequestDto;
import sirs.motorist.prototype.model.entity.CarInfo;

public interface CarService {
    CarInfo getCarInfo(ConfigurationIdRequestDto request);
}
