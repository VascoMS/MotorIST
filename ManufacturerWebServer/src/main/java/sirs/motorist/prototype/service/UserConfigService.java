package sirs.motorist.prototype.service;

import sirs.motorist.prototype.model.dto.ConfigurationIdRequestDto;
import sirs.motorist.prototype.model.dto.UserPairRequestDto;
import sirs.motorist.prototype.model.entity.Configuration;

public interface UserConfigService {
    Boolean pairNewUser(UserPairRequestDto request);
    Configuration getConfiguration(String userId, String carId);
    Boolean updateConfiguration(Configuration request);
    Boolean deleteConfiguration(ConfigurationIdRequestDto request);
}
