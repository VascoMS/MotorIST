package sirs.motorist.prototype.service;

import sirs.motorist.prototype.model.dto.ProtectedCarInfoDto;
import sirs.motorist.prototype.model.dto.InfoGetterDto;

public interface CarService {
    ProtectedCarInfoDto getCarInfo(InfoGetterDto request);
}
