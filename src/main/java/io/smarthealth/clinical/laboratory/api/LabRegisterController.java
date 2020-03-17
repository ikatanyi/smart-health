package io.smarthealth.clinical.laboratory.api;

import io.smarthealth.clinical.laboratory.data.LabRegisterData;
import io.smarthealth.clinical.laboratory.data.LabRegisterTestData;
import io.smarthealth.clinical.laboratory.data.LabResultData;
import io.smarthealth.clinical.laboratory.data.StatusRequest;
import io.smarthealth.clinical.laboratory.domain.LabRegister;
import io.smarthealth.clinical.laboratory.domain.enumeration.LabTestStatus;
import io.smarthealth.clinical.laboratory.service.LaboratoryService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
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
public class LabRegisterController {

    private final LaboratoryService service;

    public LabRegisterController(LaboratoryService service) {
        this.service = service;
    }

    @PostMapping("/labs/register")
    public ResponseEntity<?> createLabRequest(@Valid @RequestBody LabRegisterData data) {
        LabRegister item = service.createLabRegister(data);

        Pager<LabRegisterData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Lab Request Created.");
        pagers.setContent(item.toData(false));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

//    @PostMapping("/labs/register/batch")
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
    @GetMapping("/labs/register/{labNo}")
    public ResponseEntity<?> getLabRequest(@PathVariable(value = "labNo") String labNo, @RequestParam(value = "expand", required = false) Boolean expand) {

        LabRegister request = service.getLabRegisterByNumber(labNo);
        return ResponseEntity.ok(request.toData(isExpanded(expand)));
    }

    @PutMapping("/labs/register/{labNo}/tests/{id}")
    public ResponseEntity<?> updateLabRegisterTests(@PathVariable(value = "labNo") String labNo, @PathVariable(value = "id") Long testId, @Valid @RequestBody StatusRequest status) {
        int results = service.updateLabRegisteredTest(labNo, testId, status);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/labs/register/{labNo}/tests")
    public ResponseEntity<?> getLabRequestTests(@PathVariable(value = "labNo") String labNo) {
        LabRegister item = service.getLabRegisterByNumber(labNo);
        List<LabRegisterTestData> list = item.getTests()
                .stream().map(x -> x.toData(false))
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    @GetMapping("/labs/register/{labNo}/results")
    public ResponseEntity<?> getLabRegisterResults(@PathVariable(value = "labNo") String labNo) {
        LabRegister request = service.getLabRegisterByNumber(labNo);
        List<LabResultData> results = service.getResultByRegister(request)
                .stream().map(x -> x.toData())
                .collect(Collectors.toList());

        return ResponseEntity.ok(results);
    }

    @PutMapping("/labs/register/{id}")
    public ResponseEntity<?> updateLabRequest(@PathVariable(value = "id") Long id, @Valid @RequestBody LabRegisterData data) {
        LabRegister item = service.updateLabRegister(id, data);
        return ResponseEntity.ok(item.toData(false));
    }

    @DeleteMapping("/labs/register/{id}")
    public ResponseEntity<?> deleteLabRequest(@PathVariable(value = "id") Long id) {
        service.voidLabRegister(id);
        return ResponseEntity.accepted().build();
    }
//String labNumber, String orderNumber, String visitNumber,String patientNumber, TestStatus status

    @GetMapping("/labs/register")
    public ResponseEntity<?> getLabRequests(
            @RequestParam(value = "expand", required = false) Boolean expand,
            @RequestParam(value = "labNo", required = false) String labNumber,
            @RequestParam(value = "orderNo", required = false) String orderNumber,
            @RequestParam(value = "visitNo", required = false) String visitNumber,
            @RequestParam(value = "patientNo", required = false) String patientNumber,
            @RequestParam(value = "status", required = false) LabTestStatus status,
             @ApiParam(value = "Date Range", required = false, example = "2020-03-01..2020-03-17") @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
         final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
         
        Page<LabRegisterData> list = service.getLabRegister(labNumber, orderNumber, visitNumber, patientNumber, status, range,pageable)
                .map(x -> x.toData(isExpanded(expand)));

        Pager<List<LabRegisterData>> pagers = new Pager();
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

    @GetMapping("/labs/register/{visitNo}/test-results")
    public ResponseEntity<?> getLabResults(@PathVariable(value = "visitNo") String visitNo,
            @RequestParam(value = "lab_no", required = false) String labNumber) {
        List<LabRegisterTestData> labRegisterTests = service.getTestsResultsByVisit(visitNo, labNumber)
                .stream().map(x -> x.toData(true))
                .collect(Collectors.toList());
        return ResponseEntity.ok(labRegisterTests);
    }

    private Boolean isExpanded(Boolean expand) {
        return expand != null ? expand : false;
    }

    private Boolean includeResults(Boolean includeResults) {
        return includeResults != null ? includeResults : false;
    }
}
