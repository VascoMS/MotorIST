package sirs.motorist.prototype.service;

import sirs.motorist.prototype.model.entity.Configuration;

public interface UserConfigService {
    Configuration getConfiguration(String userId, String carId);
    Boolean updateConfiguration(String userId, String carId, String configuration);
    Boolean resetConfiguration(String userId, String carId);
}
