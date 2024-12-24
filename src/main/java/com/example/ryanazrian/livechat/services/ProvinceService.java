package com.example.ryanazrian.livechat.services;

import com.example.ryanazrian.livechat.model.Province;
import com.example.ryanazrian.livechat.model.Regency;
import com.example.ryanazrian.livechat.payload.region.ProvinceRegencyResponse;
import com.example.ryanazrian.livechat.payload.region.ProvinceResponse;
import com.example.ryanazrian.livechat.repository.ProvinceRepository;
import com.example.ryanazrian.livechat.repository.RegencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ProvinceService {

    @Autowired
    ProvinceRepository provinceRepository;

    @Autowired
    RegencyRepository regencyRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String KEY = "GET_PROVINCE_BY_ID";

    private static final Logger logger = LoggerFactory.getLogger(ProvinceService.class);


    public List<ProvinceResponse> getAllProvince() {
        List<Province> provinces = provinceRepository.findAll();
        List<ProvinceResponse> provinceResponses = new ArrayList<ProvinceResponse>();

        for (Province province : provinces) {
            provinceResponses.add(new ProvinceResponse(province.getId(), province.getName()));
        }

        return provinceResponses;
    }

    public Page<Province> getAllProvincePagination(PageRequest pageRequest) {
        return provinceRepository.findAll(pageRequest);
    }

    public ProvinceResponse getProvinceById(Long id) {
        Object cachedData = redisTemplate.opsForValue().get(KEY + id);

        if (cachedData instanceof LinkedHashMap) {
            LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) cachedData;

            // Manually extract the fields and create a ProvinceResponse object
            Long ids = ((Number) map.get("id")).longValue();
            String name = (String) map.get("name");

            return new ProvinceResponse(ids, name);
        }

        Province provinceData = (Province) provinceRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Province not found"));
        ProvinceResponse provinceResponse = new ProvinceResponse(provinceData.getId(), provinceData.getName());
        redisTemplate.opsForValue().set(KEY + id, provinceResponse, 5, TimeUnit.MINUTES);

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
