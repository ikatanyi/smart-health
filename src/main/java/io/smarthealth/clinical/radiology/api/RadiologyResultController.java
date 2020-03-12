//package io.smarthealth.clinical.radiology.api;
//
//import io.smarthealth.clinical.laboratory.api.*;
//import io.smarthealth.clinical.laboratory.data.LabResultData;
//import io.smarthealth.clinical.laboratory.domain.LabResult;
//import io.smarthealth.clinical.laboratory.service.LaboratoryService;
//import io.smarthealth.clinical.radiology.service.RadiologyService;
//import io.smarthealth.infrastructure.common.PaginationUtil;
//import io.smarthealth.infrastructure.lang.DateRange;
//import io.smarthealth.infrastructure.utility.PageDetails;
//import io.smarthealth.infrastructure.utility.Pager;
//import io.swagger.annotations.Api;
//import java.util.List;
//import java.util.stream.Collectors;
//import javax.validation.Valid;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// *
// * @author Kelsas
// */
//@Api
//@RestController
//@RequestMapping("/api")
//public class RadiologyResultController {
//
//    private final RadiologyService service;
//
//    public RadiologyResultController(RadiologyService service) {
//        this.service = service;
//    }
//
//    
//    @PostMapping("/radiology/results")
//    public ResponseEntity<?> createRadiologyResult(@Valid @RequestBody LabResultData data) {
//        LabResult results = service.createLabResult(data);
//        return ResponseEntity.status(HttpStatus.CREATED).body(results.toData());
//    }
//
//    @PostMapping("/radiology/results/batch")
//    public ResponseEntity<?> createLabResult(@Valid @RequestBody List<LabResultData> data) {
//
//        List<LabResultData> lists = service.createLabResult(data)
//                .stream()
//                .map(x -> x.toData())
//                .collect(Collectors.toList());
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(lists);
//    }
//
//    @GetMapping("/radiology/results/{id}")
//    public ResponseEntity<?> getLabResult(@PathVariable(value = "id") Long id) {
//        LabResult request = service.getResult(id);
//        return ResponseEntity.ok(request.toData());
//    }
//
//    @PutMapping("/radiology/results/{id}")
//    public ResponseEntity<?> updateLabResult(@PathVariable(value = "id") Long id, @Valid @RequestBody LabResultData data) {
//        LabResult item = service.updateLabResult(id, data);
//        return ResponseEntity.ok(item.toData());
//    }
//
//    @DeleteMapping("/radiology/results/{id}")
//    public ResponseEntity<?> deleteLabResult(@PathVariable(value = "id") Long id) {
//        service.voidLabResult(id);
//        return ResponseEntity.accepted().build();
//    }
// //String visitNumber, String patientNumber, String labNumber, Boolean walkin, String testName, DateRange range, Pageable page
//    @GetMapping("/radiology/results")
//    public ResponseEntity<?> getLabResults(
//            @RequestParam(value = "visit_no", required = false) String visitNumber,
//            @RequestParam(value = "patient_no", required = false) String patientNumber,
//            @RequestParam(value = "lab_no", required = false) String labNumber,
//            @RequestParam(value = "is_walkin", required = false) Boolean walkin,
//            @RequestParam(value = "test_name", required = false) String testName,
//            @RequestParam(value = "order_no", required = false) String orderNo,
//            @RequestParam(value = "dateRange", required = false) String dateRange,
//            @RequestParam(value = "page", required = false) Integer page,
//            @RequestParam(value = "pageSize", required = false) Integer size) {
//
//        final DateRange range = DateRange.fromIsoString(dateRange);
//        Pageable pageable = PaginationUtil.createPage(page, size);
//        Page<LabResultData> list = service.getLabResults(visitNumber, patientNumber, labNumber, walkin, testName,orderNo, range, pageable)
//                .map(x -> x.toData());
//
//        Pager<List<LabResultData>> pagers = new Pager();
//        pagers.setCode("0");
//        pagers.setMessage("Success");
//        pagers.setContent(list.getContent());
//        PageDetails details = new PageDetails();
//        details.setPage(list.getNumber() + 1);
//        details.setPerPage(list.getSize());
//        details.setTotalElements(list.getTotalElements());
//        details.setTotalPage(list.getTotalPages());
//        details.setReportName("Lab Results list");
//        pagers.setPageDetails(details);
//        return ResponseEntity.ok(pagers);
//    } 
//}
