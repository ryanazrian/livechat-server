package com.example.ryanazrian.livechat.security.pemdi;

import com.example.ryanazrian.livechat.chat.ChatRooms;
import com.example.ryanazrian.livechat.chat.PemdiChatHandshakeInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * STOMP-layer auth for the /ws-chat endpoint (the browser connects direct to this service,
 * so the gateway's auth is not in this path). Enforces PEMDI auth on CONNECT and DM
 * membership on SUBSCRIBE. Native /chat + /chatTopic demo endpoints are NOT affected.
 */
@Component
public class PemdiStompChannelInterceptor implements ChannelInterceptor {

    private final PemdiAuthService pemdiAuthService;

    public PemdiStompChannelInterceptor(PemdiAuthService pemdiAuthService) {
        this.pemdiAuthService = pemdiAuthService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            // Only enforce auth on sessions that connected via the secure /ws-chat endpoint.
            // The native /chat + /chatTopic demo endpoints remain unauthenticated.
            Map<String, Object> attrs = accessor.getSessionAttributes();
            boolean secureSession = attrs != null
                    && Boolean.TRUE.equals(attrs.get(PemdiChatHandshakeInterceptor.SESSION_ATTR));
            if (!secureSession) {
                return message;
            }
            String token = firstNonNull(
                    accessor.getFirstNativeHeader("Authorization"),
                    queryToken(attrs));
            PemdiUser user = pemdiAuthService.authenticate(token);
            if (user == null) {
                throw new IllegalArgumentException("Unauthorized: invalid or expired token");
            }
            accessor.setUser(user);
        } else if (StompCommand.SUBSCRIBE.equals(command)) {
            authorizeSubscribe(accessor);
        }

        return message;
    }

    private void authorizeSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null) {
            return;
        }
        if (destination.startsWith("/topic/chat/global")) {
            requireUser(accessor);
        } else if (destination.startsWith(ChatRooms.DM_SUBSCRIBE_PREFIX)) {
            PemdiUser user = requireUser(accessor);
            // /topic/chat/dm/{roomKey} -> caller must be one of the two participants
            String roomKey = destination.substring(ChatRooms.DM_SUBSCRIBE_PREFIX.length());
            if (!ChatRooms.isParticipant(roomKey, user.getSub())) {
                throw new IllegalArgumentException("Forbidden: not a member of this conversation");
            }
        }
    }

    private PemdiUser requireUser(StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();
        if (!(principal instanceof PemdiUser)) {
            throw new IllegalArgumentException("Unauthorized: not authenticated");
        }
        return (PemdiUser) principal;
    }

    @SuppressWarnings("unchecked")
    private static String queryToken(Map<String, Object> attrs) {
        if (attrs == null) {
            return null;
        }
        Object captured = attrs.get(PemdiChatHandshakeInterceptor.TOKEN_ATTR);
        if (captured instanceof String && !((String) captured).isEmpty()) {
            return (String) captured;
        }
        Object params = attrs.get("org.springframework.web.socket.URI_QUERY_PARAMETERS_ATTRIBUTE");
        if (params instanceof Map) {
            Object token = ((Map<String, List<String>>) params).get("token");
            if (token instanceof List && !((List<String>) token).isEmpty()) {
                return ((List<String>) token).get(0);
            }
        }
        return null;
    }

    private static String firstNonNull(String a, String b) {
        return a != null ? a : b;
    }
}
