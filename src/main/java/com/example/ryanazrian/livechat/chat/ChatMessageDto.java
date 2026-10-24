package com.example.ryanazrian.livechat.chat;

import java.time.Instant;

/**
 * Transport/cache projection of a {@link ChatMessage}. Kept separate from the JPA entity so the
 * Redis recent-window cache stores a clean, lazy-free, Jackson-friendly POJO (GenericJackson2
 * adds an {@code @class} type so it round-trips back to this class).
 */
public class ChatMessageDto {
    private String id;
    private String roomKey;
    private String roomType;
    private String senderId;
    private String senderName;
    private String senderRole;
    private String content;
    private Instant createdAt;

    public ChatMessageDto() {
    }

    public static ChatMessageDto from(ChatMessage m) {
        ChatMessageDto d = new ChatMessageDto();
        d.id = m.getId() == null ? null : m.getId().toString();
        d.roomKey = m.getRoomKey();
        d.roomType = m.getRoomType() == null ? null : m.getRoomType().name();
        d.senderId = m.getSenderId();
        d.senderName = m.getSenderName();
        d.senderRole = m.getSenderRole();
        d.content = m.getContent();
        d.createdAt = m.getCreatedAt();
        return d;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomKey() {
        return roomKey;
    }

    public void setRoomKey(String roomKey) {
        this.roomKey = roomKey;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
