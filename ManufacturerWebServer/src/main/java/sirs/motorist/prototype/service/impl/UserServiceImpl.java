package sirs.motorist.prototype.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sirs.motorist.prototype.model.dto.UserCredentialsDto;
import sirs.motorist.prototype.model.entity.User;
import sirs.motorist.prototype.repository.UserRepository;
import sirs.motorist.prototype.service.UserService;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.Base64;


@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean newUser(UserCredentialsDto request) {
        if (userRepository.findByUserId(request.getUserId()) != null) {
            logger.error("User {} already exists", request.getUserId());
            return false;
        }
        byte[] salt = generateSalt();

        String hashedPassword;
        try {
            hashedPassword = hashPassword(request.getPassword(), salt);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Password hashing failed while creating user: {}", e.getMessage());
            return false;
        }

        User newUser = new User(request.getUserId(),  hashedPassword, salt, null);
        userRepository.save(newUser);
        return true;
    }

    @Override
    public boolean checkCredentials(String userId, String password) {
        User storedUser = userRepository.findByUserId(userId);

        String inputHashedPassword;
        try {
            inputHashedPassword = hashPassword(password, storedUser.getSalt());
        } catch (NoSuchAlgorithmException e) {
            logger.error("Password hashing failed while checking credentials: {}", e.getMessage());
            return false;
        }
        return inputHashedPassword.equals(storedUser.getPassword());
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16-byte salt
        random.nextBytes(salt);
        return salt;
    }

    private String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(salt);
        byte[] hashedBytes = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashedBytes);
    }
}
