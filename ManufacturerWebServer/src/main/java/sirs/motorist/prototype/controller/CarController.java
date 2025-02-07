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
import sirs.motorist.prototype.model.dto.ProtectedCarInfoDto;
import sirs.motorist.prototype.model.dto.InfoGetterDto;
import sirs.motorist.prototype.service.CarService;
import sirs.motorist.prototype.service.UserService;

@RestController
@RequestMapping("/car")
public class CarController {

    private static final Logger logger = LoggerFactory.getLogger(CarController.class);

    UserService userService;
    CarService carService;
    Check check;

    @Autowired
    public CarController(CarService carService, Check nonceChecker, UserService userService) {
        this.carService = carService;
        this.check = nonceChecker;
        this.userService = userService;
    }

    @PostMapping("/readCarInfo")
    public ResponseEntity<?> readCarInfo(@RequestBody InfoGetterDto request) {
        if(!userService.checkCredentials(request.getUserId(), request.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
        if(!check.verifyNonce(request.getNonce())) {
            return ResponseEntity.badRequest().body("Nonce verification failed");
        }
        ProtectedCarInfoDto response = carService.getCarInfo(request);
        if (response == null) {
            logger.error("Car info for that car was not found...");
            return ResponseEntity.badRequest().body("Car info for that car was not found...");
        }
        logger.info("Car info: " + response);
        return ResponseEntity.ok(response);
    }
}
