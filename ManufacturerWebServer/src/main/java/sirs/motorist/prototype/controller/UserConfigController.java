package sirs.motorist.prototype.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sirs.motorist.prototype.model.dto.ConfigurationDto;
import sirs.motorist.prototype.model.dto.ConfigurationIdRequestDto;
import sirs.motorist.prototype.model.dto.PairingRequestDto;
import sirs.motorist.prototype.service.CarService;

@RestController
@RequestMapping("/user")
public class UserConfigController {

    private static final Logger logger = LoggerFactory.getLogger(UserConfigController.class);

    UserConfigService userConfigService;

    @Autowired
    public UserConfigController(UserConfigService userConfigService) {
        this.userConfigService = userConfigService;
    }

    @PostMapping("/pair")
    public ResponseEntity<?> pairNewUser(@RequestBody PairingRequestDto request) {
        //TODO: to implement
        return ResponseEntity.ok(":)");
    }

    @PostMapping("/readConfig")
    public ResponseEntity<?> readCurrentConfig(@RequestBody ConfigurationIdRequestDto request) { // TODO: BIG MAYBE to use cookies in the future
        Configuration config = userConfigService.getConfiguration(request.getUserId(), request.getCarId());
        if (config == null) {
            logger.error("Configuration for that user and car was not found...");
            return ResponseEntity.badRequest().body("Configuration for that user and car was not found...");
        }
        ConfigurationDto response = new ConfigurationDto(config.getUserId(), config.getCarId(), config.getConfiguration());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateConfig")
    public ResponseEntity<?> updateConfig(@RequestBody ConfigurationDto request) {
        if(!userConfigService.updateConfiguration(request.getUserId(), request.getCarId(), request.getConfiguration())) {
            return ResponseEntity.badRequest().body("Error updating configuration");
        }
        return ResponseEntity.ok("Configuration updated successfully");
    }

    @DeleteMapping("/deleteConfig")
    public ResponseEntity<?> deleteConfig() {
        return null; //TODO: to implement
    }
}
