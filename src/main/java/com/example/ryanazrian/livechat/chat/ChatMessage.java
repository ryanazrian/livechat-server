package com.example.ryanazrian.livechat.chat;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * A persisted chat message (durable history). Lives in the {@code pemdi_chat} schema under the
 * {@code pemdi} profile. Denormalizes sender name/role so history renders without touching auth.
 */
@Entity
@Table(name = "chat_message",
        indexes = @Index(name = "idx_chat_room_created", columnList = "roomKey,createdAt"))
public class ChatMessage {

    @Id
    @GeneratedValue(generator = "chat-uuid")
    @org.hibernate.annotations.GenericGenerator(name = "chat-uuid", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String roomKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomType roomType;

    @Column(nullable = false)
    private String senderId;

    private String senderName;

    private String senderRole;

    @Column(nullable = false, length = 4000)
    private String content;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getRoomKey() {
        return roomKey;
    }

    public void setRoomKey(String roomKey) {
        this.roomKey = roomKey;
    }

    public ChatRoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(ChatRoomType roomType) {
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
