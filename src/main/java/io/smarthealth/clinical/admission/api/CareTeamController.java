/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.api;

import io.smarthealth.clinical.admission.data.AdmissionData;
import io.smarthealth.clinical.admission.data.CareTeamData;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.service.CareTeamService;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Simon.waweru
 */
@Api
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CareTeamController {

    private final CareTeamService careTeamService;

    @PostMapping("/care-team")
//    @PreAuthorize("hasAuthority('create_care_team')")
    public ResponseEntity<?> createCareTeam(@Valid @RequestBody List<CareTeamData> data) {
        List<CareTeamData> ct = careTeamService.createCareTeam(data).stream()
                .map(e -> CareTeamData.map(e))
                .collect(Collectors.toList());

        Pager< List<CareTeamData>> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Care Team successfully submitted");
        pagers.setContent(ct);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/care-team/{admissionNumber}")
//    @PreAuthorize("hasAuthority('view_care_team')")
    public ResponseEntity<?> viewCareTeam(
            @PathVariable("admissionNumber") final String admissionNumber
    ) {

        List<CareTeamData> ct = careTeamService.fetchCareTeamByAdmissionNumber(admissionNumber).stream()
                .map(e -> CareTeamData.map(e))
                .collect(Collectors.toList());

        Pager< List<CareTeamData>> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Care team successfully submitted");
        pagers.setContent(ct);

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }
    
    @PutMapping("/care-team/{id}")
//    @PreAuthorize("hasAuthority('create_admission')")
    public ResponseEntity<?> updateCareTeam(@PathVariable("id") Long id, @Valid @RequestBody CareTeamData careTeamData) {

        CareTeamData a = CareTeamData.map(careTeamService.updateCareTeam(id, careTeamData));

        Pager<CareTeamData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Care team Updated successfully");
        pagers.setContent(a);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

}
