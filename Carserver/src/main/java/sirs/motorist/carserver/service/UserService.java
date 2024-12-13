package sirs.motorist.carserver.service;

import sirs.motorist.carserver.model.entity.User;
import sirs.motorist.carserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
