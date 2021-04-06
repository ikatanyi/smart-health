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
import io.smarthealth.organization.person.domain.WalkIn;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.smarthealth.organization.person.service.WalkingService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LabRegisterController {

    private final LaboratoryService service;
    private final WalkingService walkinService;
    private final AuditTrailService auditTrailService;

//    public LabRegisterController(LaboratoryService service) {
//        this.service = service;
//    }

    @PostMapping("/labs/register")
    @PreAuthorize("hasAuthority('create_labregister')")
    public ResponseEntity<?> createLabRequest(@Valid @RequestBody LabRegisterData data) {
       
        LabRegister item = service.createLabRegister(data);
        auditTrailService.saveAuditTrail("Laboratory", "Acknowledged lab request orderNo"+item.getOrderNumber());
        Pager<LabRegisterData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Lab Request Created.");
        pagers.setContent(item.toData(false));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/labs/register/{labNo}")
    @PreAuthorize("hasAuthority('view_labregister')")
    public ResponseEntity<?> getLabRequest(@PathVariable(value = "labNo") String labNo, @RequestParam(value = "expand", required = false) Boolean expand) {

        LabRegister request = service.getLabRegisterByNumber(labNo);
        auditTrailService.saveAuditTrail("Laboratory", "Viewed lab register test identified by labNo "+labNo);
        return ResponseEntity.ok(request.toData(isExpanded(expand)));
    }

    @PutMapping("/labs/register/{labNo}/tests/{id}")
    @PreAuthorize("hasAuthority('edit_labregister')")
    public ResponseEntity<?> updateLabRegisterTests(@PathVariable(value = "labNo") String labNo, @PathVariable(value = "id") Long testId, @Valid @RequestBody StatusRequest status) {
        int results = service.updateLabRegisteredTest(labNo, testId, status);
        auditTrailService.saveAuditTrail("Laboratory", "Update lab register test identified by testId "+testId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/labs/register/{labNo}/tests")
    @PreAuthorize("hasAuthority('view_labregister')")
    public ResponseEntity<?> getLabRequestTests(@PathVariable(value = "labNo") String labNo) {
        LabRegister item = service.getLabRegisterByNumber(labNo);
        auditTrailService.saveAuditTrail("Laboratory", "Viewed lab test identified by LabNo "+labNo);
        List<LabRegisterTestData> list = item.getTests()
                .stream().map(x -> x.toData(false))
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    @GetMapping("/labs/register/{labNo}/results")
    @PreAuthorize("hasAuthority('view_labregister')")
    public ResponseEntity<?> getLabRegisterResults(@PathVariable(value = "labNo") String labNo) {
        LabRegister request = service.getLabRegisterByNumber(labNo);
        List<LabResultData> results = service.getResultByRegister(request)
                .stream().map(x -> {
                    auditTrailService.saveAuditTrail("Laboratory", "Viewed lab test results for "+x.getAnalyte());
                    return x.toData();
                        })
                .collect(Collectors.toList());

        return ResponseEntity.ok(results);
    }

    @PutMapping("/labs/register/{id}")
    @PreAuthorize("hasAuthority('edit_labregister')")
    public ResponseEntity<?> updateLabRequest(@PathVariable(value = "id") Long id, @Valid @RequestBody LabRegisterData data) {
        LabRegister item = service.updateLabRegister(id, data);
        auditTrailService.saveAuditTrail("Laboratory", "Edited lab test identified by labNo "+item.getLabNumber());
        return ResponseEntity.ok(item.toData(false));
    }

    @DeleteMapping("/labs/register/{id}")
    @PreAuthorize("hasAuthority('delete_labregister')")
    public ResponseEntity<?> deleteLabRequest(@PathVariable(value = "id") Long id) {
        service.voidLabRegister(id);
        auditTrailService.saveAuditTrail("Laboratory", "Deleted lab test identified by id "+id);
        return ResponseEntity.accepted().build();
    }
//String labNumber, String orderNumber, String visitNumber,String patientNumber, TestStatus status

    @GetMapping("/labs/register")
    @PreAuthorize("hasAuthority('view_labregister')")
    public ResponseEntity<?> getLabRequests(
            @RequestParam(value = "expand", required = false) Boolean expand,
            @RequestParam(value = "labNo", required = false) String labNumber,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "orderNo", required = false) String orderNumber,
            @RequestParam(value = "visitNo", required = false) String visitNumber,
            @RequestParam(value = "patientNo", required = false) String patientNumber,
            @RequestParam(value = "status", required = false) List<LabTestStatus> status,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
         final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
         
        Page<LabRegisterData> list = service.getLabRegister(labNumber, orderNumber, visitNumber, patientNumber, status, range,search,pageable)
                .map(x -> {
                    LabRegisterData data =  x.toData(isExpanded(expand));
                    if(x.getIsWalkin()){
                       Optional<WalkIn> walkin = walkinService.fetchWalkingByWalkingNo(data.getPatientNo());
                       if(walkin.isPresent())
                           data.setPatientName(walkin.get().getFullName());
                    }  
                   auditTrailService.saveAuditTrail("Laboratory", "viewed lab test "+x.getLabNumber());
                   return data;
                });

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
    @PreAuthorize("hasAuthority('view_labregister')")
    public ResponseEntity<?> getLabResults(@PathVariable(value = "visitNo") String visitNo,
            @RequestParam(value = "lab_no", required = false) String labNumber) {
        List<LabRegisterTestData> labRegisterTests = service.getTestsResultsByVisit(visitNo, labNumber)
                .stream().map(x -> x.toData(true))
                .collect(Collectors.toList());
        auditTrailService.saveAuditTrail("Laboratory", "viewed lab test results identified by visitNo "+visitNo);
        return ResponseEntity.ok(labRegisterTests);
    }

    private Boolean isExpanded(Boolean expand) {
        return expand != null ? expand : false;
    }

    private Boolean includeResults(Boolean includeResults) {
        return includeResults != null ? includeResults : false;
    }
}
