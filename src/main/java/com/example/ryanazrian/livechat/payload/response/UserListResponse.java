package com.example.ryanazrian.livechat.payload.response;

import com.example.ryanazrian.livechat.model.User;

import java.util.List;

public class UserListResponse {
    private List<User> userList;

    public UserListResponse(List<User> userList) {
        this.userList = userList;
    }
}
