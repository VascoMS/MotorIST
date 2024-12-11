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
import sirs.motorist.prototype.model.dto.FirmwareRequestDto;
import sirs.motorist.prototype.model.dto.SignedFirmwareDto;
import sirs.motorist.prototype.service.FirmwareService;

@RestController
@RequestMapping("/firmware")
public class FirmwareController {

    private static final Logger logger = LoggerFactory.getLogger(FirmwareController.class);

    FirmwareService firmwareService;

    @Autowired
    public FirmwareController(FirmwareService firmwareService) {
        this.firmwareService = firmwareService;
    }

    @PostMapping("/download")
    public ResponseEntity<?> downloadFirmware(@RequestBody FirmwareRequestDto request) {
        if (!firmwareService.checkMechanicSignature(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized mechanic. Fuck off...");
        }
        SignedFirmwareDto response = firmwareService.fetchAndSignFirmware(request.getChassisNumber());
        return ResponseEntity.ok(response);
    }
}
