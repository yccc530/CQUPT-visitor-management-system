package edu.cqupt.visitor.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class RevokedTokenStore {

    private final Map<String, Instant> revokedTokens = new ConcurrentHashMap<>();

    public void revoke(String token, Instant expiresAt) {
        cleanExpiredTokens();
        revokedTokens.put(token, expiresAt);
    }

    public boolean isRevoked(String token) {
        cleanExpiredTokens();
        Instant expiresAt = revokedTokens.get(token);
        return expiresAt != null && expiresAt.isAfter(Instant.now());
    }

    private void cleanExpiredTokens() {
        Instant now = Instant.now();
        revokedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}