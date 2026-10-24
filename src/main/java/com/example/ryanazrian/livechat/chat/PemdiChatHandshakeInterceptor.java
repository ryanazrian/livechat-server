package com.example.ryanazrian.livechat.chat;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * Marks SockJS/WS sessions established through the secure /ws-chat endpoint so the
 * STOMP interceptor knows to enforce PEMDI auth for them. The native /chat and /chatTopic
 * demo endpoints are not marked, so their (unauthenticated) behavior is unchanged.
 */
public class PemdiChatHandshakeInterceptor implements HandshakeInterceptor {

    public static final String SESSION_ATTR = "pemdiChatSecure";
    public static final String TOKEN_ATTR = "pemdiChatToken";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        attributes.put(SESSION_ATTR, Boolean.TRUE);
        // Some clients (and the documented fallback) pass ?token=... on the WS/SockJS URL.
        // Capture it here so the STOMP CONNECT can read it from session attributes.
        String token = UriComponentsBuilder.fromUri(request.getURI())
                .build().getQueryParams().getFirst("token");
        if (token != null && !token.isEmpty()) {
            attributes.put(TOKEN_ATTR, token);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
