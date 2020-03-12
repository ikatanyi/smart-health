/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.api;

import io.smarthealth.clinical.radiology.data.PatientScanRegisterData;
import io.smarthealth.clinical.radiology.data.PatientScanTestData;
import io.smarthealth.clinical.radiology.domain.PatientScanRegister;
import io.smarthealth.clinical.radiology.service.RadiologyService;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/api")
@Api(value = "Patient-Radiology-Controller", description = "Radiology Patient Results Rest Controller")
public class PatientRadiologyController {

    @Autowired
    RadiologyService radiologyService;

    @Autowired
    ModelMapper modelMapper;   
    
    @PostMapping("/patient-scan")
    public @ResponseBody
    ResponseEntity<?> createPatientScan(@RequestBody final PatientScanRegisterData patientRegData, @RequestParam(value = "visitNo", required = false) final String visitNo, @RequestParam(value = "requestId", required = false) final Long requestId) {
        PatientScanRegisterData Patientscans = radiologyService.savePatientResults(patientRegData, visitNo, requestId).todata();
        Pager<PatientScanRegisterData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(Patientscans);
        PageDetails details = new PageDetails();
        details.setReportName("Patient Scans Tests");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK)
                .body(pagers);
    }
    
    @GetMapping("/patient-scan/test/{scanAccessionNo}")
    public @ResponseBody
    ResponseEntity<?> fetchPatientTestsByAccessionNo(@PathVariable("scanAccessionNo") final String scanAccessionNo) {
        PatientScanRegister scanReg = radiologyService.findPatientRadiologyTestByIdWithNotFoundDetection(scanAccessionNo);
        List<PatientScanTestData> patientLabTests = scanReg.getPatientScanTest()
                .stream()
                .map((scanTest)->(scanTest.toData()))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(patientLabTests);
    }

//    @PutMapping("/patient-scan/test/{testId}")
//    public @ResponseBody
//    ResponseEntity<?> updateRadiolgyTest(@PathVariable("resultId") final Long resultId, @Valid @RequestBody PatientScanTestData resultData) {
//        PatientScanTest r = radiologyService.findPatientRadiologyTestByIdWithNotFoundDetection(resultId);
//        r.setComments(resultData.getComments());
////        r.setResult(resultData.getResult());
//        r.setStatus(resultData.getStatus());
//
//        PatientScanTest savedResult = radiologyService.updateRadiologyResult(r);
//        return ResponseEntity.status(HttpStatus.OK).body(savedResult.toData());
//    }
//
//    @GetMapping("/patient-scan/{id}")
//    public ResponseEntity<?> fetchPatientScanById(@PathVariable("id") final Long id) {
//        PatientScanTestData result = radiologyService.findResultsByIdWithNotFoundDetection(id).toData();
//        Pager<PatientScanTestData> pagers = new Pager();
//
//        pagers.setCode("0");
//        pagers.setMessage("Success");
//        pagers.setContent(result);
//        PageDetails details = new PageDetails();
//        details.setReportName("Patient Lab Tests");
//        pagers.setPageDetails(details);
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(pagers);
//    }
    
//    @PostMapping("/patient-scan/{resultsId}/image")
//    @ApiOperation(value = "Upload/Update a scan's image details", response = Portrait.class)
//    public @ResponseBody
//    ResponseEntity<PortraitData> postScanImage(@PathVariable("resultsId") final Long resultsId,
//            @RequestParam final MultipartFile image) {
//        if (image == null) {
//            throw APIException.badRequest("Image not found");
//        }
//
//        PatientScanTest patient = radiologyService.findResultsByIdWithNotFoundDetection(resultsId);
//
//        try {
//            ImageUtil util = new ImageUtil();
//            //delete if any existing
//            util.deleteImage(image.getName());
//            ClinicalImage portrait = util.createImage(patient, image);
//            URI location = fromCurrentRequest().buildAndExpand(portrait.getId()).toUri();
//            PortraitData data = modelMapper.map(portrait, PortraitData.class);
//            return ResponseEntity.created(location).body(data);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            throw APIException.internalError("Error saving Patient Scan image ", ex.getMessage());
//        }
//
//    }
    
}
