package sirs.motorist.prototype.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pt.tecnico.sirs.model.Nonce;

@Getter
@Setter
@AllArgsConstructor
public class CarPairRequestDto {
    private String code;
    private String carId;
    private Nonce nonce;
}
