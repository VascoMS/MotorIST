package sirs.motorist.prototype.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.tecnico.sirs.secdoc.Check;
import sirs.motorist.prototype.model.dto.FirmwareRequestDto;
import sirs.motorist.prototype.model.dto.SignedFirmwareDto;
import sirs.motorist.prototype.service.FirmwareService;
import sirs.motorist.prototype.service.UserService;

@RestController
@RequestMapping("/firmware")
public class FirmwareController {

    private static final Logger logger = LoggerFactory.getLogger(FirmwareController.class);
    UserService userService;
    FirmwareService firmwareService;
    Check check;

    @Autowired
    public FirmwareController(FirmwareService firmwareService, UserService userService, Check check) {
        this.firmwareService = firmwareService;
        this.userService = userService;
        this.check = check;
    }

    @PostMapping("/download")
    public ResponseEntity<?> downloadFirmware(@RequestBody FirmwareRequestDto request) {
        if(!userService.checkCredentials(request.getUserId(), request.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
        if(!check.verifyNonce(request.getNonce())) {
            return ResponseEntity.badRequest().body("Nonce verification failed");
        }
        if (!firmwareService.checkMechanicSignature(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized mechanic!!");
        }
        SignedFirmwareDto response = firmwareService.fetchAndSignFirmware(request.getChassisNumber());
        return ResponseEntity.ok(response);
    }
}
