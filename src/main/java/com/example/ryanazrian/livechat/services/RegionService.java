package com.example.ryanazrian.livechat.services;

import com.example.ryanazrian.livechat.model.Regency;
import com.example.ryanazrian.livechat.payload.region.RegencyResponse;
import com.example.ryanazrian.livechat.repository.RegencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RegionService {
    @Autowired
    RegencyRepository regencyRepository;

    public List<RegencyResponse> getAllRegency() {
        List<Regency> regencies = regencyRepository.findAll();
        List<RegencyResponse> regencyResponses = new ArrayList<>();

        for (Regency regency : regencies) {
            regencyResponses.add(new RegencyResponse(regency.getId(), regency.getName(), regency.getProvinceId(), regency.getDistricts()));
        }

        return regencyResponses;
    }

    public List<RegencyResponse> getRegencyByProvinceId(Long provinceId) {
        List<Regency> regencies = regencyRepository.findByProvinceId(provinceId);
        List<RegencyResponse> regencyResponses = new ArrayList<>();

        for (Regency regency : regencies) {
            regencyResponses.add(new RegencyResponse(regency.getId(), regency.getName(), provinceId, regency.getDistricts()));
        }

        return regencyResponses;
    }

}
