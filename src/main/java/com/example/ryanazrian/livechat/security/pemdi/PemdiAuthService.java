package com.example.ryanazrian.livechat.security.pemdi;

import org.springframework.stereotype.Service;

/**
 * Combines local signature verification with auth-service live-session introspection.
 * This is the single entry point both the REST filter and the STOMP interceptor use.
 */
@Service
public class PemdiAuthService {

    private final PemdiJwtVerifier verifier;
    private final PemdiIntrospectClient introspectClient;

    public PemdiAuthService(PemdiJwtVerifier verifier, PemdiIntrospectClient introspectClient) {
        this.verifier = verifier;
        this.introspectClient = introspectClient;
    }

    /** @return the authenticated user, or null if the token is invalid or the session is dead. */
    public PemdiUser authenticate(String rawToken) {
        String token = stripBearer(rawToken);
        PemdiUser user = verifier.verify(token);
        if (user == null) {
            return null;
        }
        if (!introspectClient.isActive(token)) {
            return null;
        }
        return user;
    }

    private static String stripBearer(String token) {
        if (token == null) {
            return null;
        }
        String t = token.trim();
        if (t.regionMatches(true, 0, "bearer ", 0, 7)) {
            return t.substring(7).trim();
        }
        return t;
    }
}
