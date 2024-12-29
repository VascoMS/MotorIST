package sirs.motorist.prototype.service;

import sirs.motorist.prototype.model.dto.WriteOperationDto;
import sirs.motorist.prototype.model.entity.Configuration;

public interface UserConfigService {
    Configuration getConfiguration(String userId, String carId);
    Boolean updateConfiguration(WriteOperationDto request);
    Boolean deleteConfiguration(WriteOperationDto request);
}
