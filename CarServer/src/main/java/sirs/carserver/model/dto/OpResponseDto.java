package sirs.carserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OpResponseDto {
    private String requestId;
    private final String operation = "response";
    private String content;
    private boolean success;

    public OpResponseDto(String reqId, boolean success){
        this.requestId = reqId;
        this.success = success;
    }
}
