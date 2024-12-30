package sirs.motorist.prototype.service;

import sirs.motorist.prototype.model.dto.UserCredentialsDto;

public interface UserService {
    boolean newUser(UserCredentialsDto request);
    boolean checkCredentialsAndRole(String userId, String password, boolean isMechanic);
    boolean checkCredentials(String userId, String password);
}
