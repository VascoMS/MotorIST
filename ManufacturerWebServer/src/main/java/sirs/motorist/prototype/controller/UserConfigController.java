package sirs.motorist.prototype.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.tecnico.sirs.secdoc.Check;
import sirs.motorist.prototype.model.dto.WriteOperationDto;
import sirs.motorist.prototype.model.dto.InfoGetterDto;
import sirs.motorist.prototype.model.dto.UserPairRequestDto;
import sirs.motorist.prototype.model.entity.Configuration;
import sirs.motorist.prototype.service.PairingService;
import sirs.motorist.prototype.service.UserConfigService;
import sirs.motorist.prototype.service.UserService;

@RestController
@RequestMapping("/user")
public class UserConfigController {

    private static final Logger logger = LoggerFactory.getLogger(UserConfigController.class);

    UserService userService;
    UserConfigService userConfigService;
    PairingService pairingService;
    Check check;

    @Autowired
    public UserConfigController(UserConfigService userConfigService, PairingService pairingService, Check check, UserService userService) {
        this.userConfigService = userConfigService;
        this.pairingService = pairingService;
        this.check = check;
        this.userService = userService;
    }

    @PostMapping("/pair")
    public ResponseEntity<?> pairNewUser(@RequestBody UserPairRequestDto request) {
        if(!userService.checkCredentials(request.getUserId(), request.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
        if(!check.verifyNonce(request.getNonce())) {
            return ResponseEntity.badRequest().body("Nonce verification failed");
        }
        if(!pairingService.validatePairingSession(request)) {
            return ResponseEntity.badRequest().body("Error pairing new user");
        }
        return ResponseEntity.ok("User paired successfully");
    }

    @PostMapping("/readConfig")
    public ResponseEntity<?> readCurrentConfig(@RequestBody InfoGetterDto request) {
        if(!userService.checkCredentials(request.getUserId(), request.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
        if(!check.verifyNonce(request.getNonce())) {
            return ResponseEntity.badRequest().body("Nonce verification failed");
        }
        Configuration response = userConfigService.getConfiguration(request.getUserId(), request.getCarId());
        if (response == null) {
            logger.error("Configuration for that user and car was not found...");
            return ResponseEntity.badRequest().body("Configuration for that user and car was not found...");
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateConfig")
    public ResponseEntity<?> updateConfig(@RequestBody WriteOperationDto request) {
        if(!userService.checkCredentials(request.getUserId(), request.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
        if(!check.verifyNonce(request.getNonce())) {
            return ResponseEntity.badRequest().body("Nonce verification failed");
        }
        if(!userConfigService.updateConfiguration(request)) {
            return ResponseEntity.badRequest().body("Error updating configuration");
        }
        return ResponseEntity.ok("Configuration updated successfully");
    }

    @PutMapping("/deleteConfig")
    public ResponseEntity<?> deleteConfig(@RequestBody WriteOperationDto request) {
        if(!userService.checkCredentials(request.getUserId(), request.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
        if(!check.verifyNonce(request.getNonce())) {
            return ResponseEntity.badRequest().body("Nonce verification failed");
        }
        if(!userConfigService.deleteConfiguration(request)) {
            return ResponseEntity.badRequest().body("Error deleting the configuration and user");
        }
        return ResponseEntity.ok("User and car configuration were deleted successfully");
    }
}
