package com.example.ryanazrian.livechat.controllers;

import com.example.ryanazrian.livechat.payload.region.ProvinceRegencyResponse;
import com.example.ryanazrian.livechat.services.ProvinceService;
import com.example.ryanazrian.livechat.services.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/region")
public class RegionController {
    @Autowired
    RegionService regionService;

    @Autowired
    ProvinceService provinceService;

    @GetMapping("/allProvince")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> allProvince() {
        return new ResponseEntity<>(provinceService.getAllProvince(), HttpStatus.OK);
    }

    @GetMapping("/allProvincePagination")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> allProvincePagination(@RequestParam(value = "offset", required = false) Integer offset,
                                                                @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                @RequestParam(value = "sortBy", required = false) String sortBy) {
        if(null == offset) offset = 0;
        if(null == pageSize) pageSize = 10;
        if(StringUtils.isEmpty(sortBy)) sortBy ="id";
        return new ResponseEntity<>(provinceService.getAllProvincePagination(PageRequest.of(offset, pageSize, Sort.by(sortBy))), HttpStatus.OK);
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
    public ResponseEntity<List<ProvinceRegencyResponse>> allProvinceRegency() {
        return new ResponseEntity<>(provinceService.getProvincesWithRegencies(), HttpStatus.OK);
    }

    @GetMapping("/provinceRegency/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<ProvinceRegencyResponse> provinceRegencyById(@PathVariable Long id) {
        return new ResponseEntity<>(provinceService.getProvincesWithRegenciesById(id), HttpStatus.OK);
    }
}
