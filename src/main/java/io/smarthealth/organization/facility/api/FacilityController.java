/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.facility.data.FacilityData;
import io.smarthealth.organization.org.data.OrganizationData;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.org.domain.Organization;
import io.smarthealth.organization.facility.service.FacilityService;
import io.smarthealth.organization.org.service.OrganizationService;
import io.smarthealth.supplier.data.SupplierData;
import io.smarthealth.supplier.domain.Supplier;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping; 
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class FacilityController {

    private final FacilityService service;

    public FacilityController(FacilityService service) {
        this.service = service;
    }

    @PostMapping("/organization/{orgId}/facility")
    public @ResponseBody
    ResponseEntity<?> createFacility(@PathVariable(name = "orgId") String id, @RequestBody @Valid final FacilityData facilityData) {

        Facility result = service.createFacility(id, facilityData);
        Pager<Facility> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Facility created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/organization/{orgId}/facility/{code}")
    public FacilityData getFacility(@PathVariable(value = "orgId") String orgId, @PathVariable(value = "code") Long code) {
        Facility facility = service.findFacility(orgId, code);
        return FacilityData.map(facility);
    }

    @GetMapping("/organization/{orgId}/facility")
    public @ResponseBody
    ResponseEntity<?> getOrganizationFacility(@PathVariable(name = "orgId") String id) {

        List<FacilityData> list = service.findByOrganization(id)
                .stream()
                .map(data -> FacilityData.map(data)).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.CREATED).body(list);
    }
}
