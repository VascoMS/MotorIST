package com.sirs.carserver.lib.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProtectedObject {
    private String content;
    private String iv;
    private String secretKey;
    private Nonce nonce;
    private String hmac;
}
