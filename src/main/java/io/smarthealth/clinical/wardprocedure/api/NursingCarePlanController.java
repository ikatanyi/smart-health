/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.wardprocedure.api;

import io.smarthealth.clinical.wardprocedure.data.NursingCarePlanData;
import io.smarthealth.clinical.wardprocedure.domain.NursingCarePlan;
import io.smarthealth.clinical.wardprocedure.service.NursingCarePlanService;
import io.smarthealth.infrastructure.utility.ListData;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class NursingCarePlanController {

    private final NursingCarePlanService nursingCarePlanService;

    //create
    @PostMapping("/nursing-careplan")
//    @PreAuthorize("hasAuthority('create_nursing_careplan')")
    public ResponseEntity<?> createNursingCarePlan(@Valid @RequestBody NursingCarePlanData nursingCarePlanData) {
        NursingCarePlan nursingCarePlan = nursingCarePlanService.createNursingCarePlan(nursingCarePlanData);

        Pager<NursingCarePlanData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Nursing care plan successfully submitted");
        pagers.setContent(NursingCarePlanData.map(nursingCarePlan));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    //read
    @GetMapping("/nursing-careplan/{id}")
//    @PreAuthorize("hasAuthority('view_nursing_careplan')")
    public ResponseEntity<?> findNursingCarePlanById(
            @PathVariable("id") final Long id
    ) {

        NursingCarePlan nursingCarePlan = nursingCarePlanService.fetchNursingCarePlanById(id);

        Pager<NursingCarePlanData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Nursing care plan data");
        pagers.setContent(NursingCarePlanData.map(nursingCarePlan));

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    //read all 
    @GetMapping("/nursing-careplan/admission/{admissionNumber}")
//    @PreAuthorize("hasAuthority('view_nursing_careplan')")
    public ResponseEntity<?> findNursingCarePlanByAdmissionNumber(
            @PathVariable("admissionNumber") final String admissionNumber) {

        List<NursingCarePlanData> list = nursingCarePlanService.fetchNursingCarePlanByAdmissionNumber(admissionNumber).stream().map(t -> NursingCarePlanData.map(t)).collect(Collectors.toList());

        ListData<NursingCarePlanData> listData = new ListData();
        listData.setCode("200");
        listData.setMessage("Success");
        listData.setContent(list);

        return ResponseEntity.status(HttpStatus.OK).body(listData);
    }

    //update
    @PutMapping("/nursing-careplan/{id}")
//    @PreAuthorize("hasAuthority('update_nursing_careplan')")
    public ResponseEntity<?> updateNursingCarePlan(
            @PathVariable("id") final Long id,
            @Valid @RequestBody NursingCarePlanData nursingCarePlanData) {
        NursingCarePlan nursingCarePlan = nursingCarePlanService.updateNursingCarePlan(id, nursingCarePlanData);

        Pager<NursingCarePlanData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Nursing care plan successfully updated");
        pagers.setContent(NursingCarePlanData.map(nursingCarePlan));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    //delete
    @DeleteMapping("/nursing-careplan/{id}")
//    @PreAuthorize("hasAuthority('update_nursing_careplan')")
    public ResponseEntity<?> removeNursingCarePlan(@PathVariable("id") final Long id) {
        nursingCarePlanService.removeNursingCarePlan(id);

        Pager<NursingCarePlanData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Nursing care plan successfully deleted");

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }
}
