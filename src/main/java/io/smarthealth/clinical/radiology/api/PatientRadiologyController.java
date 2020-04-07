package io.smarthealth.clinical.radiology.api;

import io.smarthealth.clinical.radiology.data.PatientScanRegisterData;
import io.smarthealth.clinical.radiology.data.PatientScanTestData;
import io.smarthealth.clinical.radiology.data.RadiologyResultData;
import io.smarthealth.clinical.radiology.domain.PatientScanRegister;
import io.smarthealth.clinical.radiology.domain.PatientScanTest;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.clinical.radiology.service.RadiologyService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/api")
@Api(value = "Patient-Radiology-Controller", description = "Radiology Patient Results Rest Controller")
public class PatientRadiologyController {

    @Autowired
    RadiologyService radiologyService;

    @Autowired
    ModelMapper modelMapper;   
    
    @PostMapping("/patient-scan")
    public @ResponseBody
    ResponseEntity<?> createPatientScan(@RequestBody final PatientScanRegisterData patientRegData, @RequestParam(value = "visitNo", required = false) final String visitNo) {
        PatientScanRegisterData Patientscans = radiologyService.savePatientResults(patientRegData, visitNo).todata();
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

    @PutMapping("/patient-scan/test/{testId}")
    public @ResponseBody
    ResponseEntity<?> updateRadiolgyTest(@PathVariable("testId") final Long testId, @Valid @RequestBody PatientScanTestData patientScanTestData) {
        PatientScanTest savedResult = radiologyService.updatePatientScanTest(testId, patientScanTestData);
        return ResponseEntity.status(HttpStatus.OK).body(savedResult.toData());
    }

    @GetMapping("/patient-scan/{id}")
    public ResponseEntity<?> fetchPatientScanById(@PathVariable("id") final Long id) {
        PatientScanTestData result = radiologyService.findPatientRadiologyTestByIdWithNotFoundDetection(id).toData();
        Pager<PatientScanTestData> pagers = new Pager();

        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(result);
        PageDetails details = new PageDetails();
        details.setReportName("Patient Radiology Tests");
        pagers.setPageDetails(details);

        return ResponseEntity.status(HttpStatus.OK)
                .body(pagers);
    }
    
    @GetMapping("/patient-scan")
    public ResponseEntity<?> getRadiologyPatientTests(
            @RequestParam(value = "visit_no", required = false) String visitNumber,
            @RequestParam(value = "patient_no", required = false) String patientNumber,
            @RequestParam(value = "scan_no", required = false) String scanNumber,
            @RequestParam(value = "is_walkin", required = false) Boolean walkin,
            @RequestParam(value = "order_no", required = false) String orderNo,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) ScanTestState status,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<PatientScanTestData> list = radiologyService.findAllTests(patientNumber, search, orderNo, status, visitNumber, range, walkin, pageable)
                .map(x -> x.toData());

        Pager<List<PatientScanTestData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Patient Radiology Tests");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    } 
    
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
