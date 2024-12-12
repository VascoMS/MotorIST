package com.sirs.carserver.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config/user")
public class UserConfigController {

    private static final Logger logger = LoggerFactory.getLogger(UserConfigController.class);



    @GetMapping("/{id}")
    public ResponseEntity<?> getUserConfig(@PathVariable String id){
        //TODO: Implement method
        return ResponseEntity.ok("");
    }

    @PostMapping
    public ResponseEntity<?> addUserConfig(){
        //TODO: Implement method
        return ResponseEntity.ok("");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeUserConfig(@PathVariable String id){
        //TODO: Implement method
        return ResponseEntity.ok("");
    }


}
