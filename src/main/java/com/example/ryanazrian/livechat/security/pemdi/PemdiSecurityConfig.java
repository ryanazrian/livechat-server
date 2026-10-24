package com.example.ryanazrian.livechat.security.pemdi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * High-priority security chain for the chat feature only:
 *  - /api/chat/**  -> REST, guarded by PemdiAuthFilter (verified here)
 *  - /ws-chat/**   -> SockJS/STOMP handshake, allowed through here; real auth happens
 *                     per-CONNECT in PemdiStompChannelInterceptor (the gateway cannot
 *                     proxy WebSockets, so the browser connects direct to this service)
 *
 * The existing native WebSecurityConfig (no @Order = lowest precedence) keeps handling
 * everything else, so native livechat auth is completely untouched.
 */
@Configuration
@EnableWebSecurity
@Order(1)
public class PemdiSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PemdiAuthService pemdiAuthService;

    public PemdiSecurityConfig(PemdiAuthService pemdiAuthService) {
        this.pemdiAuthService = pemdiAuthService;
    }

    @Bean
    public PemdiAuthFilter pemdiAuthFilter() {
        return new PemdiAuthFilter(pemdiAuthService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/api/chat/**")
                .cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .addFilterBefore(pemdiAuthFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    // Second chain for the WebSocket handshake (permitAll; STOMP interceptor enforces auth).
    @Configuration
    @Order(2)
    public static class WsHandshakeConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/ws-chat/**")
                    .cors().and()
                    .csrf().disable()
                    .authorizeRequests().anyRequest().permitAll();
        }
    }
}
