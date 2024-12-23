package sirs.motorist.cli.model.dto;

import lombok.Getter;
import lombok.Setter;
import pt.tecnico.sirs.model.Nonce;

@Getter
@Setter
public class InfoGetterDto {

    private String userId;
    private String carId;
    private String password;
    private Nonce nonce;

    public InfoGetterDto(String userId, String carId, String password, Nonce nonce) {
        this.userId = userId;
        this.carId = carId;
        this.password = password;
        this.nonce = nonce;
    }
}
