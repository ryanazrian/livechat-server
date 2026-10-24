package com.example.ryanazrian.livechat.chat;

import com.example.ryanazrian.livechat.security.pemdi.PemdiUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Chat write/read logic: durable Postgres persistence + a Redis recent-window cache.
 * Access control (global = any user, DM = participants) is enforced here AND at the STOMP layer.
 */
@Service
public class ChatService {

    private static final String RECENT_PREFIX = "chat:recent:";

    private final ChatMessageRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final int cacheSize;

    public ChatService(ChatMessageRepository repository,
                       RedisTemplate<String, Object> redisTemplate,
                       @Value("${chat.history.cache-size:100}") int cacheSize) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        this.cacheSize = cacheSize;
    }

    /** Validates access, persists to Postgres, updates the Redis recent window. */
    public ChatMessageDto send(PemdiUser user, String roomKey, String content) {
        assertCanPost(roomKey, user.getSub());

        String trimmed = content == null ? "" : content.trim();
        if (trimmed.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pesan tidak boleh kosong");
        }
        if (trimmed.length() > 4000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pesan terlalu panjang");
        }

        ChatMessage entity = new ChatMessage();
        entity.setRoomKey(roomKey);
        entity.setRoomType(ChatRooms.isGlobal(roomKey) ? ChatRoomType.GLOBAL : ChatRoomType.DM);
        entity.setSenderId(user.getSub());
        entity.setSenderName(user.getDisplayName());
        entity.setSenderRole(user.getRole());
        entity.setContent(trimmed);
        entity.setCreatedAt(Instant.now());
        repository.save(entity);

        ChatMessageDto dto = ChatMessageDto.from(entity);
        cachePush(roomKey, dto);
        return dto;
    }

    /**
     * Backfill history. With {@code after == null} returns the recent window (Redis first,
     * Postgres warm-on-miss); with {@code after} returns messages newer than the cursor.
     */
    public List<ChatMessageDto> history(String roomKey, PemdiUser user, Instant after) {
        assertCanRead(roomKey, user.getSub());

        if (after != null) {
            List<ChatMessageDto> out = new ArrayList<>();
            for (ChatMessage m : repository.findSince(roomKey, after)) {
                out.add(ChatMessageDto.from(m));
            }
            return out;
        }

        List<ChatMessageDto> cached = cacheRead(roomKey);
        if (!cached.isEmpty()) {
            return cached;
        }

        List<ChatMessage> rows = new ArrayList<>(repository.findTop50ByRoomKeyOrderByCreatedAtDesc(roomKey));
        Collections.reverse(rows); // -> chronological
        List<ChatMessageDto> out = new ArrayList<>();
        for (ChatMessage m : rows) {
            ChatMessageDto dto = ChatMessageDto.from(m);
            out.add(dto);
            cachePush(roomKey, dto); // warm the window
        }
        return out;
    }

    /** Conversation list for the caller: always the global room + any DMs they belong to. */
    public List<String> rooms(PemdiUser user) {
        List<String> rooms = new ArrayList<>();
        rooms.add(ChatRooms.GLOBAL_KEY);
        for (String key : repository.findDmRoomKeys()) {
            if (ChatRooms.isParticipant(key, user.getSub())) {
                rooms.add(key);
            }
        }
        return rooms;
    }

    /** Resolves/creates a DM room key between the caller and {@code otherUserId}. */
    public String dmRoom(String me, String otherUserId) {
        return ChatRooms.dmKey(me, otherUserId);
    }

    // ---- access control ----

    private void assertCanRead(String roomKey, String sub) {
        if (ChatRooms.isGlobal(roomKey)) {
            return;
        }
        if (ChatRooms.isDm(roomKey) && ChatRooms.isParticipant(roomKey, sub)) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tidak dapat mengakses percakapan ini");
    }

    private void assertCanPost(String roomKey, String sub) {
        assertCanRead(roomKey, sub);
    }

    // ---- redis recent-window (newest-first list, capped) ----

    private void cachePush(String roomKey, ChatMessageDto dto) {
        try {
            String key = RECENT_PREFIX + roomKey;
            redisTemplate.opsForList().leftPush(key, dto);
            redisTemplate.opsForList().trim(key, 0, cacheSize - 1L);
        } catch (Exception ignored) {
            // cache is best-effort; Postgres remains the source of truth
        }
    }

    private List<ChatMessageDto> cacheRead(String roomKey) {
        List<ChatMessageDto> out = new ArrayList<>();
        try {
            List<Object> raw = redisTemplate.opsForList().range(RECENT_PREFIX + roomKey, 0, -1);
            if (raw != null) {
                for (Object o : raw) {
                    if (o instanceof ChatMessageDto) {
                        out.add((ChatMessageDto) o);
                    }
                }
            }
        } catch (Exception ignored) {
            return Collections.emptyList(); // fall back to DB
        }
        Collections.reverse(out); // Redis newest-first -> chronological
        return out;
    }
}
