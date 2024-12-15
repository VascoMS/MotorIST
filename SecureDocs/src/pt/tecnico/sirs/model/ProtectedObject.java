package pt.tecnico.sirs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProtectedObject {
    private String content;
    private String iv;
    private Nonce nonce;
    private String signature;
}
