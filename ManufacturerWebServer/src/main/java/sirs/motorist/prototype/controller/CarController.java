package sirs.motorist.prototype.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import sirs.motorist.prototype.model.dto.ConfigurationIdRequestDto;

@RestController
@RequestMapping("/car")
public class CarController {

    @PostMapping("/readCarInfo")
    public ResponseEntity<?> readCarInfo(@RequestBody ConfigurationIdRequestDto request) {
        if(check.verifyNonce(request.getNonce())) {
            return ResponseEntity.badRequest().body("Nonce verification failed");
        }
        String response = userConfigService.getCarInfo(request.getCarId());
        if (response == null) {
            logger.error("Car info for that car was not found...");
            return ResponseEntity.badRequest().body("Car info for that car was not found...");
        }
        return ResponseEntity.ok(response);
    }
}
