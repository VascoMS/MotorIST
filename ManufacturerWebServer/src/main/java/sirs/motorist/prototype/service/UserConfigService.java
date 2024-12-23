package sirs.motorist.prototype.service;

import sirs.motorist.prototype.model.dto.ConfigurationDto;
import sirs.motorist.prototype.model.dto.DeleteConfigDto;
import sirs.motorist.prototype.model.entity.Configuration;

public interface UserConfigService {
    Configuration getConfiguration(String userId, String carId);
    Boolean updateConfiguration(ConfigurationDto request);
    Boolean deleteConfiguration(DeleteConfigDto request);
}
