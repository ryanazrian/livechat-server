package com.example.ryanazrian.livechat.repository;

import com.example.ryanazrian.livechat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String Email);
    Optional<User> findByUsername(String username);
}
