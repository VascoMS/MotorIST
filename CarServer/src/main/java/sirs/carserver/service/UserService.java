package sirs.carserver.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import pt.tecnico.sirs.model.ProtectedObject;
import pt.tecnico.sirs.secdoc.Check;
import pt.tecnico.sirs.secdoc.Protect;
import pt.tecnico.sirs.secdoc.Unprotect;
import sirs.carserver.model.User;
import org.springframework.stereotype.Service;
import sirs.carserver.repository.UserRepository;
import sirs.motorist.common.Config;

import javax.crypto.spec.SecretKeySpec;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final KeyStoreService keyStoreService;
    private final String carId;
    private final String filepath;
    private final Check check;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository,Check check, KeyStoreService keyStoreService, @Value("${car.id}") String carId, @Value("${audit.file.path}") String filepath) {
        this.userRepository = userRepository;
        this.keyStoreService = keyStoreService;
        this.carId = carId;
        this.check = check;
        this.filepath = filepath;
    }

    public void createUser(String userId) throws IOException {
        Config config = new Config();

        Protect protect = new Protect();
        SecretKeySpec secretKeySpec = keyStoreService.getSecretKeySpec(userId);

        ProtectedObject protectedConfig = protect.protect(secretKeySpec, config, false);

        User user = new User(userId, protectedConfig.getContent(), protectedConfig.getIv());

        userRepository.save(user);

        //write onto the auditFile
        appendFileAudit(userId, "createUser");
    }

    @Transactional
    public boolean updateConfig(String username, ProtectedObject protectedObject) {
        SecretKeySpec secretKeySpec = keyStoreService.getSecretKeySpec(username);
        User user = userRepository.findByUsername(username);
        if(user == null){
            logger.error("User not found: {}", username);
            return false;
        }
        if (secretKeySpec == null) {
            logger.error("Secret Key not found for user: {}", username);
            return false;
        }
        //Unprotect the content so we can verify if everything is good for the check
        Unprotect unprotect = new Unprotect();
        ProtectedObject unprotectedObject = unprotect.unprotect(protectedObject, secretKeySpec);

        //Check if object was tampered with
        if(check.check(unprotectedObject, secretKeySpec, true)){
            user.setConfig(protectedObject.getContent());
            user.setIv(protectedObject.getIv());
            userRepository.save(user);

            logger.info("Successfully updated the config");

            //write onto the auditFile
            appendFileAudit(username, "updateConfig");

            return true;
        } else {
            logger.error("Config update failed, because the check failed");
            return false;
        }
    }

    @Transactional
    public boolean deleteConfig(String username, ProtectedObject protectedObject) {
        logger.info("Delete configuration for user: {}", username);
        SecretKeySpec secretKeySpec = keyStoreService.getSecretKeySpec(username);
        if (secretKeySpec == null) {
            logger.error("Secret Key not found for user: {}", username);
            return false;
        }
        //Unprotect the content so we can verify if everything is good for the check
        Unprotect unprotect = new Unprotect();
        ProtectedObject unprotectedObject = unprotect.unprotect(protectedObject, secretKeySpec);

        //Check if object was tampered with
        if(check.check(unprotectedObject, secretKeySpec, true)){
            userRepository.deleteById(username);
            try {
                keyStoreService.deleteKey(username);
            } catch (Exception e) {
                logger.error("Unable to delete key for user: {}", username);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
            logger.info("Successfully deleted user config");
            //write onto the auditFile
            appendFileAudit(username, "deleteConfig");
            return true;
        } else {
            logger.error("Delete config failed, because the check failed");
            return false;
        }
    }

    private void appendFileAudit(String userId, String operation){
        logger.info("Adding auditMessage onto log file");
        try (FileWriter writer = new FileWriter(filepath, true)) { // Open in append mode
            writer.write("UserId: " + userId + "Operation: " + operation+"\n");
        } catch (IOException e) {
            logger.error("Appending onto the audit file failed with the error: {}",e.getMessage());
        }
    }
}
