package com.example.ryanazrian.livechat.security.pemdi;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * Secures the chat REST surface (/api/chat/**) with PEMDI auth. Independent of the native
 * livechat AuthTokenFilter (which keeps owning /api/auth/**, /api/task/**, /api/region/**).
 */
public class PemdiAuthFilter extends OncePerRequestFilter {

    private final PemdiAuthService pemdiAuthService;

    public PemdiAuthFilter(PemdiAuthService pemdiAuthService) {
        this.pemdiAuthService = pemdiAuthService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        PemdiUser user = pemdiAuthService.authenticate(request.getHeader("Authorization"));
        if (user != null) {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    user, null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}
