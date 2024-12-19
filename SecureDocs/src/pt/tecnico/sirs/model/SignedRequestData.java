package pt.tecnico.sirs.model;



public record SignedRequestData(String signature, Nonce nonce) {
}
