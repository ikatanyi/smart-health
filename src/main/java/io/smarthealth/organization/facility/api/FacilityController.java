/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.api;

import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.company.data.LogoResponse;
import io.smarthealth.organization.company.domain.CompanyLogo;
import io.smarthealth.organization.facility.data.FacilityData;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.facility.service.FacilityService;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    @PreAuthorize("hasAuthority('create_facility')")
    public @ResponseBody
    ResponseEntity<?> createFacility(@PathVariable(name = "orgId") String id, @RequestBody @Valid final FacilityData facilityData) throws IOException {

        Facility result = service.createFacility(id, facilityData);
        Pager<FacilityData> pagers = new Pager();

        FacilityData savedData = FacilityData.map(result);

        pagers.setCode("0");
        pagers.setMessage("Facility created successful");
        pagers.setContent(savedData);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @PutMapping("/facility/{id}")
    @PreAuthorize("hasAuthority('edit_facility')")
    public @ResponseBody
    ResponseEntity<?> updateFacility(@PathVariable(name = "id") Long id, @RequestBody @Valid final FacilityData facilityData) {

        Facility facility = service.findFacility(id);

        Facility result = service.updateFacility(facility, facilityData);
        Pager<FacilityData> pagers = new Pager();

        FacilityData savedData = FacilityData.map(result);

        pagers.setCode("0");
        pagers.setMessage("Facility updated successful");
        pagers.setContent(savedData);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/organization/{orgId}/facility/{code}")
    @PreAuthorize("hasAuthority('view_facility')")
    public FacilityData getFacility(@PathVariable(value = "orgId") String orgId, @PathVariable(value = "code") Long code) {
        Facility facility = service.findFacility(orgId, code);
        return FacilityData.map(facility);
    }

    @GetMapping("/organization/{orgId}/facility")
    @PreAuthorize("hasAuthority('view_facility')")
    public @ResponseBody
    ResponseEntity<?> getOrganizationFacility(@PathVariable(name = "orgId") String id) {

        List<FacilityData> list = service.findByOrganization(id)
                .stream()
                .map(data -> FacilityData.map(data)).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.CREATED).body(list);
    }

    //Your logo has been saved.
    @PostMapping("/facility/{facilityId}/logo")
    @PreAuthorize("hasAuthority('create_company')")
    public LogoResponse uploadLogo(@PathVariable("facilityId") final Long facilityId, @RequestParam("file") MultipartFile file) {
        CompanyLogo logo = service.storeLogo(facilityId, file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/facility/logo/")
                .path(String.valueOf(logo.getId()))
                .toUriString();
        return new LogoResponse(logo.getFileName(), fileDownloadUri, file.getContentType(), file.getSize());
    }

    @GetMapping("/facility/logo/{logoId}")
    @PreAuthorize("hasAuthority('view_company')")
    public ResponseEntity<Resource> downloadLogo(@PathVariable Long logoId) {
        // Load file from database
        CompanyLogo dbFile = service.getLogo(logoId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dbFile.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dbFile.getFileName() + "\"")
                .body(new ByteArrayResource(dbFile.getData()));
    }

    @DeleteMapping("/facility/logo/{logoId}")
    @PreAuthorize("hasAuthority('delete_company')")
    public ResponseEntity<?> deleteLogo(@PathVariable Long logoId) {
        service.deleteLogo(logoId);
        return ResponseEntity.noContent().build();
    }
}
