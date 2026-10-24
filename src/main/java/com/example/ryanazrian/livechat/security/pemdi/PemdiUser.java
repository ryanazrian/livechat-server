package com.example.ryanazrian.livechat.security.pemdi;

import java.security.Principal;

/**
 * Authenticated PEMDI user, built from a verified PEMDI access-token (and confirmed by
 * auth-service introspection). Used as the STOMP Principal and the REST SecurityContext
 * principal for the chat feature. Independent of the native livechat UserDetailsImpl.
 */
public class PemdiUser implements Principal {
    private final String sub;
    private final String email;
    private final String role;
    private final String name;
    private final String unitKerjaId;

    public PemdiUser(String sub, String email, String role, String name, String unitKerjaId) {
        this.sub = sub;
        this.email = email;
        this.role = role;
        this.name = name;
        this.unitKerjaId = unitKerjaId;
    }

    /** STOMP Principal name = PEMDI user id (sub). */
    @Override
    public String getName() {
        return sub;
    }

    public String getSub() {
        return sub;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getDisplayName() {
        return name;
    }

    public String getUnitKerjaId() {
        return unitKerjaId;
    }
}
