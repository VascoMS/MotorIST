package sirs.motorist.cli.model;

import pt.tecnico.sirs.model.Nonce;

public record SignedRequestData(String signature, Nonce nonce) {
}
