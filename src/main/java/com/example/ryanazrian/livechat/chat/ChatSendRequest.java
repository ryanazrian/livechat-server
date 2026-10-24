package com.example.ryanazrian.livechat.chat;

public class ChatSendRequest {
    private String roomKey;
    private String content;
    // For initiating a DM, send the peer's user id instead of a precomputed roomKey.
    private String toUserId;

    public String getRoomKey() {
        return roomKey;
    }

    public void setRoomKey(String roomKey) {
        this.roomKey = roomKey;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }
}
