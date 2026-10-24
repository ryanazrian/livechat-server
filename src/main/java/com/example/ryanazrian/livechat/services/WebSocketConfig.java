package com.example.ryanazrian.livechat.services;

import com.example.ryanazrian.livechat.chat.PemdiChatHandshakeInterceptor;
import com.example.ryanazrian.livechat.security.pemdi.PemdiStompChannelInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final PemdiStompChannelInterceptor pemdiStompChannelInterceptor;
    private final String[] allowedOrigins;

    public WebSocketConfig(PemdiStompChannelInterceptor pemdiStompChannelInterceptor,
                           @Value("${chat.allowed-origins:http://localhost:3000}") String allowedOriginsCsv) {
        this.pemdiStompChannelInterceptor = pemdiStompChannelInterceptor;
        this.allowedOrigins = allowedOriginsCsv.split("\\s*,\\s*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Single call: enableSimpleBroker REPLACES the prefix set, so both prefixes must be
        // passed together (a second call would drop "/topic" and silently kill delivery).
        config.enableSimpleBroker("/topic", "/topic2");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Native livechat demo (unchanged, unauthenticated)
        registry.addEndpoint("/chat");
        registry.addEndpoint("/chat").withSockJS();

        registry.addEndpoint("/chatTopic");
        registry.addEndpoint("/chatTopic").withSockJS();

        // Secure PEMDI chat endpoint (browser connects direct; enforced per-CONNECT by interceptor)
        registry.addEndpoint("/ws-chat")
                .setAllowedOrigins(allowedOrigins)
                .addInterceptors(new PemdiChatHandshakeInterceptor())
                .withSockJS()
                .setInterceptors(new PemdiChatHandshakeInterceptor());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(pemdiStompChannelInterceptor);
    }
}
