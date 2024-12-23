package com.example.ryanazrian.livechat.controllers;

import com.example.ryanazrian.livechat.payload.region.ProvinceRegencyResponse;
import com.example.ryanazrian.livechat.repository.ProvinceRepository;
import com.example.ryanazrian.livechat.repository.RegencyRepository;
import com.example.ryanazrian.livechat.services.ProvinceService;
import com.example.ryanazrian.livechat.services.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/region")
public class RegionController {

    @Autowired
    ProvinceRepository provinceRepository;

    @Autowired
    RegencyRepository regencyRepository;

    @Autowired
    RegionService regionService;

    @Autowired
    ProvinceService provinceService;

    @GetMapping("/allProvince")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> allProvince() {
        return new ResponseEntity<>(provinceService.getAllProvince(), HttpStatus.OK);
    }

    @GetMapping("/allRegency")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> allRegency() {
        return new ResponseEntity<>(regionService.getAllRegency(), HttpStatus.OK);
    }

    @GetMapping("/provinceById/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getProvinceById(@PathVariable Long id) {
        return new ResponseEntity<>(provinceService.getProvinceById(id), HttpStatus.OK);
    }

    @GetMapping("/regencyByProvinceId/{provinceId}")
    public ResponseEntity<?> getRegencyByProvinceId(@PathVariable Long provinceId) {
        return new ResponseEntity<>(regionService.getRegencyByProvinceId(provinceId), HttpStatus.OK);
    }

    @GetMapping("/provinceRegency")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<ProvinceRegencyResponse> allProvinceRegency() {
        return provinceService.getProvincesWithRegencies();
    }

    @GetMapping("/provinceRegency/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ProvinceRegencyResponse provinceRegencyById(@PathVariable Long id) {
        return provinceService.getProvincesWithRegenciesById(id);
    }
}
