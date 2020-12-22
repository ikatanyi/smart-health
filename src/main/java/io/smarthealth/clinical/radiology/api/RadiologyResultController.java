package io.smarthealth.clinical.radiology.api;

import io.smarthealth.clinical.radiology.data.PatientScanTestData;
import io.smarthealth.clinical.radiology.data.RadiologyResultData;
import io.smarthealth.clinical.radiology.domain.RadiologyResult;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.clinical.radiology.service.RadiologyService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class RadiologyResultController {

    private final RadiologyService service;    
    private final AuditTrailService auditTrailService;

    public RadiologyResultController(RadiologyService service, AuditTrailService auditTrailService) {
        this.service = service;
        this.auditTrailService = auditTrailService;
    }

    
    @PostMapping("/radiology/results")
    @PreAuthorize("hasAuthority('create_radiologyresults')")
    public ResponseEntity<?> createRadiologyResult(@Valid @RequestBody RadiologyResultData data) {
        RadiologyResult results = service.saveRadiologyResult( data);
        auditTrailService.saveAuditTrail("Radiology", "Entered Patient Radiology results for test  "+results.getPatientScanTest().getRadiologyTest().getScanName());
        return ResponseEntity.status(HttpStatus.CREATED).body(results.toData());
    }
    
    @PostMapping("/radiology/upload-image")
    @PreAuthorize("hasAuthority('create_radiologyresults')")
    public ResponseEntity<?> uploadScanImage(Long testId, MultipartFile file) {
        PatientScanTestData results = service.uploadScanImage(testId, file).toData();
        auditTrailService.saveAuditTrail("Radiology", "Uploaded Patient Radiology image for test  "+results.getScanName());
        return ResponseEntity.status(HttpStatus.CREATED).body(results);
    }


    @GetMapping("/radiology/results/{id}")
    @PreAuthorize("hasAuthority('view_radiologyresults')")
    public ResponseEntity<?> getRadiologyResult(@PathVariable(value = "id") Long id) {
        RadiologyResult request = service.findResultsByIdWithNotFoundDetection(id);
        auditTrailService.saveAuditTrail("Radiology", "Viewed Patient Radiology test for"+request.getPatientScanTest().getRadiologyTest().getScanName());
        return ResponseEntity.ok(request.toData());
    }

    @PutMapping("/radiology/results/{id}")
    @PreAuthorize("hasAuthority('edit_radiologyresults')")
    public ResponseEntity<?> updateLabResult(@PathVariable(value = "id") Long id, MultipartFile file, @Valid @RequestBody RadiologyResultData data) {
        RadiologyResult item = service.updateRadiologyResult(id, file, data);
        auditTrailService.saveAuditTrail("Radiology", "Updated Patient Radiology test identified by id "+id);
        return ResponseEntity.ok(item.toData());
    }

//    @DeleteMapping("/radiology/results/{id}")
//    public ResponseEntity<?> deleteLabResult(@PathVariable(value = "id") Long id) {
//        service.voidRadiologyResult(id);
//        return ResponseEntity.accepted().build();
//    }
 //String visitNumber, String patientNumber, String labNumber, Boolean walkin, String testName, DateRange range, Pageable page
    @GetMapping("/radiology/results")
    public ResponseEntity<?> getRadiologyResults(
            @RequestParam(value = "visit_no", required = false) String visitNumber,
            @RequestParam(value = "patient_no", required = false) String patientNumber,
            @RequestParam(value = "scan_no", required = false) String scanNumber,
            @RequestParam(value = "is_walkin", required = false) Boolean walkin,
            @RequestParam(value = "status", required = false) ScanTestState status,
            @RequestParam(value = "order_no", required = false) String orderNo,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "search", required = false) String search, 
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<RadiologyResultData> list = service.findAllRadiologyResults(visitNumber, patientNumber, scanNumber, walkin, status, orderNo, range, search, pageable)
                .map(x -> x.toData());

        auditTrailService.saveAuditTrail("Radiology", "Viewed all Patient Radiology results done");
        Pager<List<RadiologyResultData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Radiology Results list");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    } 
}
