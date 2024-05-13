package com.example.ryanazrian.livechat.payload.region;

public class RegencyResponse {

    private Long id;
    private String name;
    private Long provinceId;

    public RegencyResponse(Long id, String name, Long provinceId) {
        this.id = id;
        this.name = name;
        this.provinceId = provinceId;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }
}
