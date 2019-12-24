/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.api;

import io.smarthealth.clinical.procedure.data.PatientProcedureRegisterData;
import io.smarthealth.clinical.procedure.data.PatientProcedureTestData;
import io.smarthealth.clinical.procedure.domain.PatientProcedureRegister;
import io.smarthealth.clinical.procedure.domain.PatientProcedureTest;
import io.smarthealth.clinical.procedure.service.ProcedureService;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kennedy.Imbenzi
 */
@RestController
@RequestMapping("/api/v1")
@Api(value = "Patient-Procedure-Controller", description = "Procedure Patient Results Rest Controller")
public class PatientProcedureController {

    @Autowired
    ProcedureService procedureService;

    @Autowired
    ModelMapper modelMapper;   
    
    @PostMapping("/patient-procedure")
    public @ResponseBody
    ResponseEntity<?> createPatientProcedure(@RequestBody final PatientProcedureRegisterData patientRegData, @RequestParam(value = "visitNo", required = false) final String visitNo, @RequestParam(value = "requestId", required = false) final Long requestId) {
        PatientProcedureRegisterData Patientprocedures = PatientProcedureRegisterData.map(procedureService.savePatientResults(patientRegData, visitNo, requestId));
        Pager<PatientProcedureRegisterData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(Patientprocedures);
        PageDetails details = new PageDetails();
        details.setReportName("Patient Procedures Tests");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK)
                .body(pagers);
    }
    
    @GetMapping("/patient-procedure/results/{procedureAccessionNo}")
    public @ResponseBody
    ResponseEntity<?> fetchPatientProceduressByAccessionNo(@PathVariable("procedureAccessionNo") final String procedureAccessionNo) {
        PatientProcedureRegister procedureReg = procedureService.findProceduresByIdWithNotFoundDetection(procedureAccessionNo);
        //find patient tests by labTestFile
        List<PatientProcedureTestData> patientLabTests = procedureReg.getPatientProcedureTest()
                .stream()
                .map((procedureTest)->(PatientProcedureTestData.map(procedureTest)))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(patientLabTests);
    }

    @PutMapping("/patient-procedure/results/{resultId}")
    public @ResponseBody
    ResponseEntity<?> updateProcedureResults(@PathVariable("resultId") final Long resultId, @Valid @RequestBody PatientProcedureTestData resultData) {
        PatientProcedureTest r = procedureService.findResultsByIdWithNotFoundDetection(resultId);
        r.setComments(resultData.getComments());
        r.setResult(resultData.getResults());
        r.setStatus(resultData.getState());

        PatientProcedureTest savedResult = procedureService.updateProcedureResult(r);
        return ResponseEntity.status(HttpStatus.OK).body(PatientProcedureTestData.map(savedResult));
    }

    @GetMapping("/patient-procedure/{id}")
    public ResponseEntity<?> fetchPatientProcedureById(@PathVariable("id") final Long id) {
        PatientProcedureTestData result = PatientProcedureTestData.map(procedureService.findResultsByIdWithNotFoundDetection(id));
        Pager<PatientProcedureTestData> pagers = new Pager();

        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(result);
        PageDetails details = new PageDetails();
        details.setReportName("Patient Lab Tests");
        pagers.setPageDetails(details);

        return ResponseEntity.status(HttpStatus.OK)
                .body(pagers);
    }    
}
