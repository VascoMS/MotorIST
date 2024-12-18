package sirs.carserver.service;

import org.springframework.stereotype.Service;
import sirs.carserver.model.PairingSession;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service
public class PairingService {
    private PairingSession pairingSession;

    public PairingService() {
        // Schedule a cleanup task to remove expired pair requests
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::cleanupExpiredRequests, 1, 1, TimeUnit.MINUTES);
    }

    public String createPairingSession() {
        if(pairingSession != null) {
            return null;
        }
        pairingSession = new PairingSession();
        return pairingSession.getCode();
    }

    public boolean checkPairingSession(String code) {
        if(pairingSession == null) {
            return false;
        }
        return pairingSession.getCode().equals(code);
    }

    public void endPairSession() {
        pairingSession = null;
    }

    private void cleanupExpiredRequests() {
        long now = System.currentTimeMillis();
        if(pairingSession != null && (now - pairingSession.getTimestamp()) > TimeUnit.MINUTES.toMillis(1)) {
            endPairSession();
        }
    }
}

