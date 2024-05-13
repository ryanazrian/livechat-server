package com.example.ryanazrian.livechat.services;

import com.example.ryanazrian.livechat.model.Province;
import com.example.ryanazrian.livechat.model.Regency;
import com.example.ryanazrian.livechat.payload.region.ProvinceResponse;
import com.example.ryanazrian.livechat.payload.region.RegencyResponse;
import com.example.ryanazrian.livechat.repository.ProvinceRepository;
import com.example.ryanazrian.livechat.repository.RegencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class RegionService {

    @Autowired
    ProvinceRepository provinceRepository;

    @Autowired
    RegencyRepository regencyRepository;

    public List<ProvinceResponse> getAllProvince() {
        List<Province> provinces = provinceRepository.findAll();
        List<ProvinceResponse> provinceResponses = new ArrayList<ProvinceResponse>();

        for (Province province : provinces) {
            provinceResponses.add(new ProvinceResponse(province.getId(), province.getName()));
        }

        return provinceResponses;
    }

    public ProvinceResponse getProvinceById(Long id) {
        Province provinceData = (Province) provinceRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Province not found"));
        ProvinceResponse provinceResponse = new ProvinceResponse(provinceData.getId(), provinceData.getName());

        return provinceResponse;
    }

    public List<RegencyResponse> getAllRegency() {
        List<Regency> regencies = regencyRepository.findAll();
        List<RegencyResponse> regencyResponses = new ArrayList<>();

        for (Regency regency : regencies) {
            regencyResponses.add(new RegencyResponse(regency.getId(), regency.getName(), regency.getProvinceId()));
        }

        return regencyResponses;
    }

    public List<RegencyResponse> getRegencyByProvinceId(Long provinceId) {
        List<Regency> regencies = regencyRepository.findByProvinceId(provinceId);
        List<RegencyResponse> regencyResponses = new ArrayList<>();

        for (Regency regency : regencies) {
            regencyResponses.add(new RegencyResponse(regency.getId(), regency.getName(), provinceId));
        }

        return regencyResponses;
    }

}
