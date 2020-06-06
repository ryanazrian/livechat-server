package com.example.ryanazrian.livechat.repository;

import com.example.ryanazrian.livechat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    interface GetUser{
        String getId();
        String getUsername();
        String getEmail();
    }

    List<GetUser> findAllBy();
}
