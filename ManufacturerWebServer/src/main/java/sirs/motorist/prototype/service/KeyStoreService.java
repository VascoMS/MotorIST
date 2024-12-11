package sirs.motorist.prototype.service;

import java.security.PrivateKey;

public interface KeyStoreService {
    PrivateKey getPrivateKey(String alias) throws Exception;
}
