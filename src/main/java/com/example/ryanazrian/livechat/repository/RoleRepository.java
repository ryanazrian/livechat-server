package com.example.ryanazrian.livechat.repository;

import com.example.ryanazrian.livechat.model.ERole;
import com.example.ryanazrian.livechat.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
