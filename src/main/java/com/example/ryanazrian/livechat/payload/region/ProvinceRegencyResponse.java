package com.example.ryanazrian.livechat.payload.region;

import com.example.ryanazrian.livechat.model.Regency;

import java.util.List;

public class ProvinceRegencyResponse {
    private Long id;
    private String name;
    private List<Regency> regencyList;

     public ProvinceRegencyResponse(Long id, String name, List<Regency> regencyList) {
         this.id = id;
         this.name = name;
         this.regencyList = regencyList;
     }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
         return name;
    }

    public void setRegencyList(List<Regency> regencyList) {
        this.regencyList = regencyList;
    }

    public List<Regency> getRegencyList() {
        return regencyList;
    }
}
