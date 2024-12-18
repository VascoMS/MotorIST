package sirs.carserver.service;

import sirs.carserver.model.Config;
import sirs.carserver.model.User;
import org.springframework.stereotype.Service;
import sirs.carserver.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final AuditService auditService;

    public UserService(UserRepository userRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    public User createUser(String username) {
        User user = new User();
        user.setUsername(username);

        Config config = new Config();
        user.setConfig(config);
        return userRepository.save(user);
    }

    //TODO whenever we edit a config or sth, an audit needs to be created + the config is a new audit, not an edit -> needs to be updated on the user

    //TODO the configUpdate needs to create a new config at some point
    public void updateConfig(String username, Config configUpdate) {
        User user = userRepository.findByUsername(username);
        user.setConfig(configUpdate);
        userRepository.save(user);

        //create the new audit
        auditService.createAudit(user, configUpdate);
    }
}
