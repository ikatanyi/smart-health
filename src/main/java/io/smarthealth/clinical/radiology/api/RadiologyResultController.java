package io.smarthealth.clinical.radiology.api;

import io.smarthealth.clinical.radiology.data.RadiologyResultData;
import io.smarthealth.clinical.radiology.domain.RadiologyResult;
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
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class RadiologyResultController {

    private final RadiologyService service;

    public RadiologyResultController(RadiologyService service) {
        this.service = service;
    }

    
    @PostMapping("/radiology/results")
    public ResponseEntity<?> createRadiologyResult(@Valid @RequestBody RadiologyResultData data) {
        RadiologyResult results = service.saveRadiologyResult(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(results.toData());
    }


    @GetMapping("/radiology/results/{id}")
    public ResponseEntity<?> getRadiologyResult(@PathVariable(value = "id") Long id) {
        RadiologyResult request = service.findResultsByIdWithNotFoundDetection(id);
        return ResponseEntity.ok(request.toData());
    }

    @PutMapping("/radiology/results/{id}")
    public ResponseEntity<?> updateLabResult(@PathVariable(value = "id") Long id, @Valid @RequestBody RadiologyResultData data) {
        RadiologyResult item = service.updateRadiologyResult(id, data);
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

        Pager<List<RadiologyResultData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Lab Results list");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    } 
}
