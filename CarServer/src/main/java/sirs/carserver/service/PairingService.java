package sirs.carserver.service;

import org.springframework.stereotype.Service;
import sirs.carserver.model.PairingSession;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PairingService {
    private AtomicReference<PairingSession> pairingSession;

    public PairingService() {
        // Schedule a cleanup task to remove expired pair requests
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::cleanupExpiredRequests, 1, 1, TimeUnit.MINUTES);
    }

    public PairingSession createPairSession() {
        PairingSession newPairingSession = new PairingSession();
        if(pairingSession.compareAndSet(null, newPairingSession)) {
            return newPairingSession;
        }
        return null;
    }

    public void endPairSession() {
        pairingSession.set(null);
    }

    private void cleanupExpiredRequests() {
        long now = System.currentTimeMillis();
        PairingSession currentPairingSession = pairingSession.get();
        if(currentPairingSession != null && (now - currentPairingSession.getTimestamp()) > TimeUnit.MINUTES.toMillis(5)) {
            endPairSession();
        }
    }
}

