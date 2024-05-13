package com.example.ryanazrian.livechat.model;

import javax.persistence.*;

@Entity
@Table(name = "provinces")
public class Province {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

//    @Transient
//    private List<Regency> regencies;

//  Merge the table
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "province")
//    private List<Regency> regencies;

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

//    public List<Regency> getRegencies() {
//        return regencies;
//    }
//
//    public void setRegencies(List<Regency> regencies) {
//        this.regencies = regencies;
//    }
}
