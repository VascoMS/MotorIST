package sirs.motorist.prototype.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sirs.motorist.prototype.model.entity.Configuration;
import sirs.motorist.prototype.repository.ConfigRepository;
import sirs.motorist.prototype.service.UserConfigService;

@Service
public class UserConfigServiceImpl implements UserConfigService {

    private static final Logger logger = LoggerFactory.getLogger(UserConfigServiceImpl.class);

    private final ConfigRepository configRepository;

    @Autowired
    public UserConfigServiceImpl(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    public Configuration getConfiguration(String userId, String carId) {
        Configuration config = configRepository.findByUserIdAndCarId(userId, carId);
        if (config == null) {
            logger.error("Configuration for that user and car was not found...");
            return null;
        }
        return config;
    }

    @Override
    public Boolean updateConfiguration(String userId, String carId, String configuration) {
        return null;
    }

    @Override
    public Boolean resetConfiguration(String userId, String carId) {
        return null;
    }
}
