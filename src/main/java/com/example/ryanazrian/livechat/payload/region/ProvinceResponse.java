package com.example.ryanazrian.livechat.payload.region;

import java.io.Serializable;

public class ProvinceResponse implements Serializable {
    private Long id;
    private String name;

    public ProvinceResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{id='" + id + "', name='" + name + "}";
    }

}
