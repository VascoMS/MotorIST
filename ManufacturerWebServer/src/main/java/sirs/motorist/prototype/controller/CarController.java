package sirs.motorist.prototype.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/car")
public class CarController {

    @PostMapping("/pair")
    public ResponseEntity<?> initPairCheck() {
        // TODO: Store pairing session in memory for posterior validation against user code
        return ResponseEntity.ok("Pairing session initiated");
    }
}
