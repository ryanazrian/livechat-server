package com.example.ryanazrian.livechat.payload.request;

import com.example.ryanazrian.livechat.model.EStatus;

import java.util.UUID;

public class TaskEditRequest {
    private UUID id;

    private String title;

    private String description;

    private EStatus status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
