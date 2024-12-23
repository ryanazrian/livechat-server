package com.example.ryanazrian.livechat.services;

import com.example.ryanazrian.livechat.model.Province;
import com.example.ryanazrian.livechat.model.Regency;
import com.example.ryanazrian.livechat.payload.region.ProvinceRegencyResponse;
import com.example.ryanazrian.livechat.payload.region.ProvinceResponse;
import com.example.ryanazrian.livechat.repository.ProvinceRepository;
import com.example.ryanazrian.livechat.repository.RegencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProvinceService {

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

    public List<ProvinceRegencyResponse> getProvincesWithRegencies() {
        return provinceRepository.findAll().stream()
                .map(province -> new ProvinceRegencyResponse(
                        province.getId(),
                        province.getName(),
                        regencyRepository.findByProvinceId(province.getId())
                ))
                .collect(Collectors.toList());
    }

    public ProvinceRegencyResponse getProvincesWithRegenciesById(Long Id) {
        Province provinceData = (Province) provinceRepository.findById(Id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Province not found with ID: " + Id));;
        List<Regency> regencies = regencyRepository.findByProvinceId(provinceData.getId());
        return new ProvinceRegencyResponse(provinceData.getId(), provinceData.getName(), regencies);
    }

}
