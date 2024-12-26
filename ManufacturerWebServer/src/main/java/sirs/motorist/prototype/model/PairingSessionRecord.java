package sirs.motorist.prototype.model;

import pt.tecnico.sirs.model.ProtectedObject;

public record PairingSessionRecord(byte[] hashedCode, ProtectedObject protectedConfig) {
}
