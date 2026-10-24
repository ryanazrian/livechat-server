package com.example.ryanazrian.livechat.chat;

import com.example.ryanazrian.livechat.security.pemdi.PemdiUser;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Collections;

/**
 * STOMP inbound handler for the secure /ws-chat endpoint. App prefix is /app, so clients
 * send to /app/chat.send. Reuses the shared message broker + native demo endpoints untouched.
 */
@Controller
public class ChatMessageController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessageController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void send(@Payload ChatSendRequest request, Principal principal) {
        PemdiUser user = (PemdiUser) principal;

        String roomKey = request.getRoomKey();
        if (roomKey == null || roomKey.trim().isEmpty()) {
            if (request.getToUserId() == null || request.getToUserId().trim().isEmpty()) {
                throw new IllegalArgumentException("roomKey atau toUserId wajib diisi");
            }
            roomKey = chatService.dmRoom(user.getSub(), request.getToUserId());
        }

        ChatMessageDto dto = chatService.send(user, roomKey, request.getContent());
        messagingTemplate.convertAndSend(ChatRooms.destination(roomKey), dto);
    }

    /** Surface handler errors to the sender instead of silently dropping them. */
    @MessageExceptionHandler
    public Object onError(IllegalArgumentException ex) {
        return Collections.singletonMap("error", ex.getMessage());
    }
}
