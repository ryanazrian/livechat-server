package com.example.ryanazrian.livechat.repository;

import com.example.ryanazrian.livechat.model.Regency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegencyRepository extends JpaRepository<Regency, Long> {
    Optional<Regency> findByName(String name);
    Optional<Regency> findById(Long aLong);

    List<Regency> findByProvinceId(Long provinceId);
}
