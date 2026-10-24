package com.example.ryanazrian.livechat.chat;

import com.example.ryanazrian.livechat.security.pemdi.PemdiUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * Chat REST surface under /api/chat/**, guarded by PemdiAuthFilter (see PemdiSecurityConfig).
 * Used for backfill/scrollback + the conversation list. Live delivery is via /ws-chat STOMP.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // PemdiUser implements Principal, so @AuthenticationPrincipal is hijacked by Spring MVC's
    // PrincipalMethodArgumentResolver (which casts request.getUserPrincipal() -> Authentication).
    // Read the principal from the SecurityContext directly instead.
    private PemdiUser user() {
        return (PemdiUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("/me")
    public PemdiUser me() {
        return user();
    }

    /** Conversation list: the global room plus any DMs the caller participates in. */
    @GetMapping("/rooms")
    public List<String> rooms() {
        return chatService.rooms(user());
    }

    /** Resolve (and return) the DM room key between the caller and another user. */
    @GetMapping("/dm")
    public String dmRoom(@RequestParam("withUserId") String withUserId) {
        return chatService.dmRoom(user().getSub(), withUserId);
    }

    /** History. after = ISO instant for incremental fetch; omit for the recent window. */
    @GetMapping("/messages")
    public List<ChatMessageDto> messages(
            @RequestParam("roomKey") String roomKey,
            @RequestParam(value = "after", required = false) Instant after) {
        return chatService.history(roomKey, user(), after);
    }
}
