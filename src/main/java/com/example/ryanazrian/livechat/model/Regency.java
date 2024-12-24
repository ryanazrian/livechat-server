package com.example.ryanazrian.livechat.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "regencies")
public class Regency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(name = "province_id")
    private Long provinceId;

//    Join the table
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "province_id", nullable = false)
//    private Province province;
//
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regency")
    private List<District> districts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }


    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }
}
