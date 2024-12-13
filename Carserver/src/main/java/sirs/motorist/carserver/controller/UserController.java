package sirs.motorist.carserver.controller;

import sirs.motorist.carserver.model.entity.User;
import sirs.motorist.carserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public User addUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable String userId) {
        return userService.getUserById(userId);
    }
}
