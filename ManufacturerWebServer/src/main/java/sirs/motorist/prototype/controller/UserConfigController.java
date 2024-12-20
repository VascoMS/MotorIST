package sirs.motorist.prototype.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sirs.motorist.prototype.model.dto.ConfigurationIdRequestDto;
import sirs.motorist.prototype.model.dto.UserPairRequestDto;
import sirs.motorist.prototype.model.entity.Configuration;
import sirs.motorist.prototype.service.UserConfigService;

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
    public ResponseEntity<?> pairNewUser(@RequestBody UserPairRequestDto request) {
        //TODO: to implement
        return ResponseEntity.ok(":)");
    }

    @PostMapping("/readConfig")
    public ResponseEntity<?> readCurrentConfig(@RequestBody ConfigurationIdRequestDto request) { // TODO: BIG MAYBE to use cookies in the future
        Configuration response = userConfigService.getConfiguration(request.getUserId(), request.getCarId());
        if (response == null) {
            logger.error("Configuration for that user and car was not found...");
            return ResponseEntity.badRequest().body("Configuration for that user and car was not found...");
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateConfig")
    public ResponseEntity<?> updateConfig(@RequestBody Configuration request) {
        if(!userConfigService.updateConfiguration(request)) {
            return ResponseEntity.badRequest().body("Error updating configuration");
        }
        return ResponseEntity.ok("Configuration updated successfully");
    }

    @PutMapping("/deleteConfig")
    public ResponseEntity<?> deleteConfig(@RequestBody ConfigurationIdRequestDto request) {
        if(!userConfigService.deleteConfiguration(request)) {
            return ResponseEntity.badRequest().body("Error resetting the configuration");
        }
        return ResponseEntity.ok("Configuration was reset successfully");
    }
}
