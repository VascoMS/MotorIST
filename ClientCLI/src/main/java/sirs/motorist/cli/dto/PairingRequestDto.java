package sirs.motorist.cli.dto;

import lombok.Getter;
import lombok.Setter;
import pt.tecnico.sirs.model.Nonce;

@Getter
@Setter
public class PairingRequestDto {

    private String userId;
    private String userSignature;
    private Nonce nonce;
    private String pairCode;

    public PairingRequestDto(String userId, String userSignature, Nonce nonce, String pairCode) {
        this.userId = userId;
        this.userSignature = userSignature;
        this.nonce = nonce;
        this.pairCode = pairCode;
    }
}
