package sirs.motorist.prototype.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sirs.motorist.prototype.service.CarService;

@RestController
@RequestMapping("/car")
public class CarsController {

    private static final Logger logger = LoggerFactory.getLogger(CarsController.class);

    CarService carService;

    @Autowired
    public CarsController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping("/pair")
    public ResponseEntity<?> pairNewUser() {
        return null; //TODO: to implement
    }

    @PostMapping("/pairCode")
    public ResponseEntity<?> sendPairCode() {
        return null; //TODO: to implement
    }

    @PostMapping("/readConfig")
    public ResponseEntity<?> readCurrentConfig() {
        return null; //TODO: to implement
    }

    @PostMapping("/newConfig")
    public ResponseEntity<?> createConfig() {
        return null; //TODO: to implement
    }

    @PutMapping("/updateConfig")
    public ResponseEntity<?> updateConfig() {
        return null; //TODO: to implement
    }

    @DeleteMapping("/deleteConfig")
    public ResponseEntity<?> deleteConfig() {
        return null; //TODO: to implement
    }
}
