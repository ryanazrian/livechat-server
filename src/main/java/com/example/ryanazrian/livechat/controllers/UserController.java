package com.example.ryanazrian.livechat.controllers;


import com.example.ryanazrian.livechat.payload.request.LoginRequest;

import com.example.ryanazrian.livechat.payload.response.UserListResponse;
import com.example.ryanazrian.livechat.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/userList")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getListuser() {
        HashMap data = new HashMap();
        data.put("status", 200);
        data.put("data", userRepository.findAllBy());

        return ResponseEntity.ok(data);
    }
}
