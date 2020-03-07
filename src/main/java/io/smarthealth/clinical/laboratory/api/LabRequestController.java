package io.smarthealth.clinical.laboratory.api;

import io.smarthealth.clinical.laboratory.data.LabRequestData;
import io.smarthealth.clinical.laboratory.data.LabRequestTestData;
import io.smarthealth.clinical.laboratory.data.StatusRequest;
import io.smarthealth.clinical.laboratory.domain.LabRequest;
import io.smarthealth.clinical.laboratory.domain.LabRequestTest;
import io.smarthealth.clinical.laboratory.domain.enumeration.TestStatus;
import io.smarthealth.clinical.laboratory.service.LaboratoryService;
import io.smarthealth.infrastructure.common.PaginationUtil;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class LabRequestController {

    private final LaboratoryService service;

    public LabRequestController(LaboratoryService service) {
        this.service = service;
    }

    @PostMapping("/labs/requests")
    public ResponseEntity<?> createLabRequest(@Valid @RequestBody LabRequestData data) {
        LabRequest item = service.createLabRequest(data);

        Pager<LabRequestData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Lab Request Created.");
        pagers.setContent(item.toData(false));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

//    @PostMapping("/labs/requests/batch")
//    public ResponseEntity<?> createLabRequest(@Valid @RequestBody List<LabRequestData> data) {
//
//        List<LabRequestData> item = service.createLabRequest(data)
//                .stream()
//                .map(x -> x.toData())
//                .collect(Collectors.toList());
//
//        Pager< List<LabRequestData>> pagers = new Pager();
//        pagers.setCode("0");
//        pagers.setMessage("Doctor Items Created.");
//        pagers.setContent(item);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
//    }
    @GetMapping("/labs/requests/{id}")
    public ResponseEntity<?> getLabRequest(@PathVariable(value = "id") String labNo, @RequestParam(value = "expand", required = false) Boolean expand) {
         
        LabRequest request = service.getLabRequestByNumber(labNo);
        return ResponseEntity.ok(request.toData(isExpanded(expand)));
    }

        @PutMapping("/labs/requests/{labNo}/tests/{id}")
    public ResponseEntity<?> updateLabRequestTests( @PathVariable(value = "labNo") String labNo,@PathVariable(value = "id") Long testId, @Valid @RequestBody StatusRequest status) {
            LabRequestTest test = service.updateLabRequestTest(labNo,testId, status); 
        return ResponseEntity.ok(test);
    }
    
    @GetMapping("/labs/requests/{id}/tests")
    public ResponseEntity<?> getLabRequestTests(@PathVariable(value = "id") String labNo) {
        LabRequest item = service.getLabRequestByNumber(labNo);
        List<LabRequestTestData> list = item.getTests()
                .stream().map(x -> x.toData())
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    @PutMapping("/labs/requests/{id}")
    public ResponseEntity<?> updateLabRequest(@PathVariable(value = "id") Long id, @Valid @RequestBody LabRequestData data) {
        LabRequest item = service.updateLabRequest(id, data);
        return ResponseEntity.ok(item.toData(false));
    }

    @DeleteMapping("/labs/requests/{id}")
    public ResponseEntity<?> deleteLabRequest(@PathVariable(value = "id") Long id) {
        service.deleteLabRequest(id);
        return ResponseEntity.accepted().build();
    }
//String labNumber, String orderNumber, String visitNumber,String patientNumber, TestStatus status

    @GetMapping("/labs/requests")
    public ResponseEntity<?> getLabRequests(
            @RequestParam(value = "expand", required = false) Boolean expand,
            @RequestParam(value = "labNo", required = false) String labNumber,
            @RequestParam(value = "orderNo", required = false) String orderNumber,
            @RequestParam(value = "visitNo", required = false) String visitNumber,
            @RequestParam(value = "patientNo", required = false) String patientNumber,
            @RequestParam(value = "status", required = false) TestStatus status,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<LabRequestData> list = service.getLabRequest(labNumber, orderNumber, visitNumber, patientNumber, status, pageable)
                .map(x -> x.toData(isExpanded(expand)));

        Pager<List<LabRequestData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Lab Request list");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    private Boolean isExpanded(Boolean expand) {
        return expand != null ? expand : false;
    }
}
