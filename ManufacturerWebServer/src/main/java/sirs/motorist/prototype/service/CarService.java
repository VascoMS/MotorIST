package sirs.motorist.prototype.service;

import sirs.motorist.prototype.model.dto.CarInfoDto;
import sirs.motorist.prototype.model.dto.InfoGetterDto;

public interface CarService {
    CarInfoDto getCarInfo(InfoGetterDto request);
}
