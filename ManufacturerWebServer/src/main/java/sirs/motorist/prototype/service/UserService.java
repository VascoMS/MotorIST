package sirs.motorist.prototype.service;

import sirs.motorist.prototype.model.dto.UserCredentialsDto;

public interface UserService {
    boolean newUser(UserCredentialsDto request);
    boolean checkCredentials(String userId, String password);
}
