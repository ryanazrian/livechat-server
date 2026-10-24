package com.example.ryanazrian.livechat.security.pemdi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Live-session check against auth-service {@code POST /auth/introspect}, mirroring the
 * gateway + arsitektur guards. Results cached per token for a short TTL. FAIL-CLOSED:
 * if auth-service is unreachable/timeout the token is treated as invalid.
 */
@Component
public class PemdiIntrospectClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String authServiceUrl;
    private final String introspectSecret;
    private final long cacheTtlMs;

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public PemdiIntrospectClient(
            @Value("${pemdi.auth.service-url}") String authServiceUrl,
            @Value("${pemdi.auth.introspect-secret}") String introspectSecret,
            @Value("${pemdi.auth.introspect-cache-ms:10000}") long cacheTtlMs) {
        this.authServiceUrl = authServiceUrl;
        this.introspectSecret = introspectSecret;
        this.cacheTtlMs = cacheTtlMs;
    }

    /** @return true if auth-service confirms the access token maps to a live session. */
    public boolean isActive(String token) {
        if (cacheTtlMs > 0) {
            CacheEntry hit = cache.get(token);
            if (hit != null && hit.expiresAt > System.currentTimeMillis()) {
                return hit.valid;
            }
        }

        boolean valid;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-introspect-key", introspectSecret);
            HttpEntity<Map<String, String>> req =
                    new HttpEntity<>(Collections.singletonMap("token", token), headers);

            Map<?, ?> resp = restTemplate.postForObject(
                    authServiceUrl + "/auth/introspect", req, Map.class);
            valid = resp != null && Boolean.TRUE.equals(resp.get("valid"));
        } catch (Exception e) {
            // fail-closed: auth unreachable -> reject
            valid = false;
        }

        if (cacheTtlMs > 0) {
            cache.put(token, new CacheEntry(valid, System.currentTimeMillis() + cacheTtlMs));
        }
        return valid;
    }

    private static final class CacheEntry {
        final boolean valid;
        final long expiresAt;

        CacheEntry(boolean valid, long expiresAt) {
            this.valid = valid;
            this.expiresAt = expiresAt;
        }
    }
}
