package sirs.carserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
public class OpResponseDto {
    private final String reqId;
    private final String operation = "response";
    private final boolean success;

    public OpResponseDto(String reqId, boolean success){
        this.reqId = reqId;
        this.success = success;
    }

}
