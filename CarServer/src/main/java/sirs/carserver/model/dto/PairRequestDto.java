package sirs.carserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PairRequestDto {
    private String carId;
    private String pairCode;
    private String base64Config;

}
