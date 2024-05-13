package com.example.ryanazrian.livechat.payload.chat;

public class OutputMessage {
    private String name;
    private String message;
    private String timestamp;

    public OutputMessage(String name, String message, String timestamp) {
        this.name = name;
        this.message = message;
        this.timestamp = timestamp;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
