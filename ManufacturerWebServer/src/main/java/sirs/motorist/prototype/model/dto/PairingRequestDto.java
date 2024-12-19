package sirs.motorist.prototype.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pt.tecnico.sirs.model.Nonce;

@Getter
@Setter
@AllArgsConstructor
public class PairingRequestDto {
    private String userId;
    private String userSignature;
    private Nonce nonce;
    private String carId;
    private String pairCode;
}
