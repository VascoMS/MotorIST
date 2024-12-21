package sirs.motorist.prototype.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.tecnico.sirs.secdoc.Check;
import sirs.motorist.prototype.model.dto.ConfigurationIdRequestDto;
import sirs.motorist.prototype.model.entity.CarInfo;
import sirs.motorist.prototype.service.CarService;

@RestController
@RequestMapping("/car")
public class CarController {

    private static final Logger logger = LoggerFactory.getLogger(CarController.class);

    CarService carService;
    Check check;

    @Autowired
    public CarController(CarService carService, Check nonceChecker) {
        this.carService = carService;
        this.check = nonceChecker;
    }

    @PostMapping("/readCarInfo")
    public ResponseEntity<?> readCarInfo(@RequestBody ConfigurationIdRequestDto request) {
        if(check.verifyNonce(request.getNonce())) {
            return ResponseEntity.badRequest().body("Nonce verification failed");
        }
        CarInfo response = carService.getCarInfo(request);
        if (response == null) {
            logger.error("Car info for that car was not found...");
            return ResponseEntity.badRequest().body("Car info for that car was not found...");
        }
        return ResponseEntity.ok(response);
    }
}
