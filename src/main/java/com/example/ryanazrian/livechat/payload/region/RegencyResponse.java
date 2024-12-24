package com.example.ryanazrian.livechat.payload.region;

import com.example.ryanazrian.livechat.model.District;

import java.util.List;

public class RegencyResponse {

    private Long id;
    private String name;
    private Long provinceId;
    private List<District> district;

    public RegencyResponse(Long id, String name, Long provinceId, List<District> district) {
        this.id = id;
        this.name = name;
        this.provinceId = provinceId;
        this.district = district;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getProvinceId() {
        return provinceId;
    }

    public List<District> getDistrict() {
        return district;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public void setDistrict(List<District> district) {
        this.district = district;
    }
}
