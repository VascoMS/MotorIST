package sirs.carserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pt.tecnico.sirs.model.Nonce;

@AllArgsConstructor
@Getter
public class OperationResponseDto {
    private String requestId;
    private final String operation = "response";
    private boolean success;
    private Nonce nonce;
}
