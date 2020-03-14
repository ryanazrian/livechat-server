package com.example.ryanazrian.livechat.service;

import com.example.ryanazrian.livechat.model.User;
import com.example.ryanazrian.livechat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User findUserByEmail(String Email) {
        return userRepository.findByEmail(Email);
    }

    public User findUserByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    public User saveUser(User user) {
        user.setPassword(user.getPassword());
        user.setUserName(user.getUserName());
        user.setEmail(user.getEmail());
//        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        //DATE
        Date date = new Date();
        user.setCreatedAt(date);
        user.setUpdatedAt(date);

        return userRepository.save(user);
    }
}
