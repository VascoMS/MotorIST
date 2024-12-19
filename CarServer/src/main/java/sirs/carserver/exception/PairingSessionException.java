package sirs.carserver.exception;

public class PairingSessionException extends Exception{
    public PairingSessionException(String message) {
        super(message);
    }

    public PairingSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
