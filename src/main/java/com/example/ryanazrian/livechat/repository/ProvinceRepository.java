package com.example.ryanazrian.livechat.repository;

import com.example.ryanazrian.livechat.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long> {
    Optional<Province> findByName(String name);

    Optional<Province> findById(Long id);

    @Override
    List<Province> findAll();
}
