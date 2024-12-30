package sirs.motorist.prototype.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sirs.motorist.prototype.model.dto.UserCredentialsDto;
import sirs.motorist.prototype.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/newUser")
    public ResponseEntity<?> newUser(@RequestBody UserCredentialsDto request) {
        if (!userService.newUser(request)) {
            logger.error("Failed to create new user: {}", request.getUserId());
            return ResponseEntity.badRequest().body("Failed to create new user");
        }
        return ResponseEntity.ok("User created successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserCredentialsDto request) {
        if (!userService.checkCredentialsAndRole(request.getUserId(), request.getPassword(), request.isMechanic())) {
            logger.error("Failed to login user: {}", request.getUserId());
            return ResponseEntity.badRequest().body("Failed to login user");
        }
        return ResponseEntity.ok("User logged in successfully");
    }
}
