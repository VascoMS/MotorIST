package sirs.motorist.prototype.service.impl;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sirs.motorist.prototype.model.dto.UserCredentialsDto;
import sirs.motorist.prototype.service.FirmwareService;
import sirs.motorist.prototype.service.UserService;

@Service
public class DBPopulationService {

    private final UserService userService;
    private final FirmwareService firmwareService;
    private static final Logger logger = LoggerFactory.getLogger(DBPopulationService.class);

    public DBPopulationService(UserService userService, FirmwareService firmwareService) {
        this.userService = userService;
        this.firmwareService = firmwareService;
    }

    @PostConstruct
    public void populateDB() {
        logger.info("Populating database with initial data");
        UserCredentialsDto user = new UserCredentialsDto("Mechanic", "pass", true);
        userService.newMechanic(user, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxyKdpRHKvTPUceAafSPqF42tx3M0ZaYFJemF93DKhRkci2kH0y4yDyE+4wOy8fIV1iwL3TKVQ3GqYX5TnxkGqFOdHP1aqpRqDrmpk6ufCcBZM88ZRDH4BmcbEfBxeXS3xw7KNuLYUUVjmEbHAr2p+gROhBS3DzZ0gx3U0+glXCbLQcThQDaWnSN+3H/hGipT5B+ynjBzegHbC6Mv2/sNyNwKOUl8t8QC7Fg4Kv4utWkDmF/xsSSxJ3D6Lw5lnFHFYdNKjQx4OtiSjkPvsNKYqRPNa7Uj8neXeejIkRR97WomBpEC9cvTUQK0OOZA4bNvlXr+cKhdQE+pCu4sl5A57QIDAQAB");
        firmwareService.addFirmware(1, "MotorIST firmware version 1.0");
        firmwareService.addFirmware(2, "MotorIST firmware version 1.0");
    }
}
