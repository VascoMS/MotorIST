package sirs.motorist.cli.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pt.tecnico.sirs.model.Nonce;

@Getter
@Setter
@AllArgsConstructor
public class UserPairRequestDto {
    private String userId;
    private String password;
    private Nonce nonce;
    private String carId;
    private String pairCode;
}
