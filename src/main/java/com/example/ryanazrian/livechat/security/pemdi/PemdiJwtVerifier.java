package com.example.ryanazrian.livechat.security.pemdi;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Verifies a PEMDI access token (HS256, shared JWT_ACCESS_SECRET) and asserts the
 * {@code type == "access"} claim. Mirrors the gateway/arsitektur guard semantics.
 * Does NOT hit auth-service on its own — pair with {@link PemdiIntrospectClient}.
 */
@Component
public class PemdiJwtVerifier {

    private final String secret;

    public PemdiJwtVerifier(@Value("${pemdi.jwt.access-secret}") String secret) {
        this.secret = secret;
    }

    /** @return the decoded user, or null if the token is invalid/expired/not an access token. */
    public PemdiUser verify(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        try {
            Claims claims = Jwts.parser()
                    // Raw UTF-8 bytes to match the Node (jsonwebtoken) services; the String
                    // overload would Base64-decode the secret and mismatch their signature.
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();

            if (!"access".equals(claims.get("type", String.class))) {
                return null;
            }

            String unitKerjaId = claims.get("unitKerjaId", String.class);
            if (unitKerjaId == null) {
                unitKerjaId = claims.get("clusterName", String.class);
            }

            return new PemdiUser(
                    claims.get("sub", String.class),
                    claims.get("email", String.class),
                    claims.get("role", String.class),
                    claims.get("name", String.class),
                    unitKerjaId);
        } catch (Exception e) {
            return null;
        }
    }
}
