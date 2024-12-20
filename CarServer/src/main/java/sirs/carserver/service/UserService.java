package sirs.carserver.service;

import org.springframework.beans.factory.annotation.Value;
import pt.tecnico.sirs.model.ProtectedObject;
import pt.tecnico.sirs.secdoc.Protect;
import sirs.carserver.model.Config;
import sirs.carserver.model.User;
import org.springframework.stereotype.Service;
import sirs.carserver.repository.UserRepository;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final KeyStoreService keyStoreService;
    private final String carId;

    public UserService(UserRepository userRepository, KeyStoreService keyStoreService, @Value("${car.id}") String carId) {
        this.userRepository = userRepository;
        this.keyStoreService = keyStoreService;
        this.carId = carId;
    }

    public void createUser(String username) throws IOException {
        Config config = new Config();

        Protect protect = new Protect();
        SecretKeySpec secretKeySpec = keyStoreService.getSecretKeySpec(username);

        Map<String, String> additionalFields = Map.of("carId", carId);
        additionalFields.put("username", username);

        ProtectedObject protectedConfig = protect.protect(secretKeySpec, config, false, additionalFields);

        User user = new User(username, protectedConfig.getContent(), protectedConfig.getIv());

        userRepository.save(user);
    }

    //TODO: finish this shit
    public void updateConfig(String username, String configUpdate) {
        User user = userRepository.findByUsername(username);
        user.setConfig(configUpdate);
        userRepository.save(user);

        //TODO: create the new audit (append to audit file)

    }
}
