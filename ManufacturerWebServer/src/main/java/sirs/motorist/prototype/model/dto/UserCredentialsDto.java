package sirs.motorist.prototype.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCredentialsDto {

    private String userId;
    private String password;
    private boolean isMechanic;

    public UserCredentialsDto(String userId, String password, boolean isMechanic) {
        this.userId = userId;
        this.password = password;
        this.isMechanic = isMechanic;
    }
}
