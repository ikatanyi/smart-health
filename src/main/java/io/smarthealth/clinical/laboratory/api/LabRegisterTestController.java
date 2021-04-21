package io.smarthealth.clinical.laboratory.api;

import io.smarthealth.clinical.laboratory.data.LabRegisterData;
import io.smarthealth.clinical.laboratory.data.LabRegisterTestData;
import io.smarthealth.clinical.laboratory.domain.enumeration.LabTestStatus;
import io.smarthealth.clinical.laboratory.service.LaboratoryService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.person.domain.WalkIn;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LabRegisterTestController {
    private final LaboratoryService service;


    @GetMapping("/labs/register-test")
    @PreAuthorize("hasAuthority('view_labregister')")
    public ResponseEntity<?> getLabRequests(
            @RequestParam(value = "labNumber", required = false) String labNumber,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam(value = "visitNumber", required = false) String visitNumber,
            @RequestParam(value = "patientNumber", required = false) String patientNumber,
            @RequestParam(value = "stockEffectCaptured", required = false) final Boolean stockEffectCaptured,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Page<LabRegisterTestData> list = service.getLabRegisterTest(labNumber,orderNumber, visitNumber,patientNumber,null,range,
                null,search, stockEffectCaptured,pageable).map((t)->{
           return t.toData(false);
        });

        Pager<List<LabRegisterTestData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Lab register test list");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

}
