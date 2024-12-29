package sirs.motorist.prototype.model.dto;

import lombok.Getter;
import lombok.Setter;
import pt.tecnico.sirs.model.Nonce;

@Getter
@Setter
public class WriteOperationDto {

    private String userId;
    private String carId;
    private String password;
    private String content;
    private String iv;
    private Nonce nonce;
    private String hmac;

    public WriteOperationDto(String userId, String carId, String password, String content, String iv, Nonce nonce, String hmac) {
        this.carId = carId;
        this.userId = userId;
        this.password = password;
        this.content = content;
        this.iv = iv;
        this.nonce = nonce;
        this.hmac = hmac;
    }
}
