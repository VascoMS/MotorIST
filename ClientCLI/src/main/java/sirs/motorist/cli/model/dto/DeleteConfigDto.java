package sirs.motorist.cli.model.dto;

import lombok.Getter;
import lombok.Setter;
import pt.tecnico.sirs.model.Nonce;

@Getter
@Setter
public class DeleteConfigDto {
    private String userId;
    private String carId;
    private String password;
    private String confirmationPhrase;
    private String iv;
    private Nonce nonce;
    private String hmac;

    public DeleteConfigDto(String userId, String carId, String password, String confirmationPhrase, String iv, Nonce nonce, String hmac) {
        this.userId = userId;
        this.carId = carId;
        this.password = password;
        this.confirmationPhrase = confirmationPhrase;
        this.iv = iv;
        this.nonce = nonce;
        this.hmac = hmac;
    }
}
