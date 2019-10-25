/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.pharmacy.api;

import io.smarthealth.clinical.lab.data.ContainerData;
import io.smarthealth.clinical.pharmacy.data.PatientDrugsData;
import io.smarthealth.clinical.pharmacy.service.PharmacyService;
import io.smarthealth.infrastructure.common.APIResponse;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.swagger.annotations.Api;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
 * @author Kennedy.Imbenzi
 */
@RestController
@RequestMapping("/api/pharmacy")
@Api(value = "Pharmacy Controller", description = "Operations pertaining to Pharmacy maintenance")
public class PharmacyController {
    @Autowired
    PharmacyService pharmService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/patientDrug")
    public @ResponseBody
    ResponseEntity<?> savePatientDrugs(@RequestBody @Valid final List<PatientDrugsData> patientdrugsData) {
        List<PatientDrugsData> patientDrugList = pharmService.savePatientDrugs(patientdrugsData);        
        HttpHeaders headers = null;//PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(patientDrugList, headers, HttpStatus.OK);
     
    }
    
     @GetMapping("/patientDrug/{id}")
    public ResponseEntity<?> fetchPatientDrugsById(@PathVariable("id") final Long id) {
        PatientDrugsData patientdrugsdata = pharmService.getById(id);
        if (patientdrugsdata != null) {
            return ResponseEntity.ok(patientdrugsdata);
        } else {
            throw APIException.notFound("container Number {0} not found.", id);
        }
    }

    @GetMapping("/patientDrug")
    public ResponseEntity<?> fetchAllPatientDrugs(
            @RequestParam(value = "visitNumber", defaultValue = "") String visitNumber,
            @RequestParam(value = "patientNumber", defaultValue = "") String patientNumber,
            Pageable pageable) {

        List<PatientDrugsData> patientDrugs = pharmService.getByVisitIdAndPatientId(visitNumber, patientNumber);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/api/lab/patientDrug/")
                .buildAndExpand().toUri();
        return ResponseEntity.created(location).body(APIResponse.successMessage("PatientDrugsData returned successfuly", HttpStatus.OK, patientDrugs));
    }

    @DeleteMapping("/patientDrug/{id}")
    public ResponseEntity<?> deleteSpecimen(@PathVariable("id") final Long id) {
//        pharmService.;
        return ResponseEntity.ok("200");
    }

    
}
