/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.api;

import io.smarthealth.infrastructure.utility.ContentPage;
import io.smarthealth.organization.facility.data.FacilityData;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.facility.service.FacilityService;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author simz
 */
@RestController
@RequestMapping("/api")
public class FacilityController {

    @Autowired
    FacilityService facilityService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/facility")
    public @ResponseBody
    ResponseEntity<?> createFacility(@RequestBody @Valid final FacilityData facilityData) {
        Facility facilityCreated = facilityService.createFacility(facilityData);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/facility/" + facilityData.getCode())
                .buildAndExpand(facilityCreated.getCode()).toUri();

        return ResponseEntity.created(location).body(facilityService.convertFacilityEntityToData(facilityCreated));
    }

    @GetMapping("/facility")
    public ResponseEntity<List<FacilityData>> fetchAllFacilities(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {

        ContentPage<FacilityData> page = facilityService.fetchAllFacilities(pageable);
        return new ResponseEntity<>(page.getContents(), HttpStatus.OK);
    }

}
