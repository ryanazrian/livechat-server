package com.example.ryanazrian.livechat.controllers;

import com.example.ryanazrian.livechat.model.User;
import com.example.ryanazrian.livechat.payload.response.UserResponse;
import com.example.ryanazrian.livechat.security.jwt.JwtUtils;
import com.example.ryanazrian.livechat.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @GetMapping("/details")
    public ResponseEntity<?> getUserById(@RequestHeader("Authorization") String token) {
        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Validate and extract user ID from the token
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired JWT token");
        }

        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userDetailsService.getByUsername(username);
        UserResponse userResponse = new UserResponse(user.getId().toString(), user.getUsername(), user.getEmail());
        userResponse.setProvince(new UserResponse.ProvinceResponse(user.getProvince().getId(), user.getProvince().getName()));

        return new ResponseEntity<>(userResponse, HttpStatus.OK);

    }
}
