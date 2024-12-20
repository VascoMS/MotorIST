package sirs.motorist.cli.dto;

import lombok.Getter;
import lombok.Setter;
import pt.tecnico.sirs.model.Nonce;

@Getter
@Setter
public class PairingRequestDto {

    private String userId;
    private String pairCode;

    public PairingRequestDto(String userId, String pairCode) {
        this.userId = userId;
        this.pairCode = pairCode;
    }
}
