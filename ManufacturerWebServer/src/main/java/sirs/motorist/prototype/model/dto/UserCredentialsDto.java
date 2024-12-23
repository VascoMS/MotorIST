package sirs.motorist.prototype.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCredentialsDto {

    private String userId;
    private String password;

    public UserCredentialsDto(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
