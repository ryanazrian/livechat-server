package com.example.ryanazrian.livechat.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class TaskRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String title;

    @NotBlank
    private String description;

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
