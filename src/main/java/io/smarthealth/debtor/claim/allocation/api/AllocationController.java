package io.smarthealth.debtor.claim.allocation.api;

import io.smarthealth.accounting.payment.domain.Remittance;
import io.smarthealth.accounting.payment.service.RemittanceService;
import io.smarthealth.debtor.claim.allocation.data.AllocationData;
import io.smarthealth.debtor.claim.allocation.domain.Allocation;
import io.smarthealth.debtor.claim.allocation.service.AllocationService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class AllocationController {

    private final AllocationService allocationService;
    private final RemittanceService remitanceService;
    private final AuditTrailService auditTrailService;

    public AllocationController(AllocationService allocationService, RemittanceService remitanceService, AuditTrailService auditTrailService) {
        this.allocationService = allocationService;
        this.remitanceService = remitanceService;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/allocation/{remmitanceId}")
    @PreAuthorize("hasAuthority('create_allocation')")
    public ResponseEntity<?> createAllocation(@PathVariable("remmitanceId") final Long remmitanceId, @Valid @RequestBody List<AllocationData> allocationData) {
        Remittance remitance = remitanceService.getRemittanceOrThrow(remmitanceId);
        List<Allocation> allocations = allocationService.createAllocation(allocationData, remitance);
        List<AllocationData> dataList = new ArrayList<>();
        allocations.stream().map((a) -> a.map()).forEachOrdered((a) -> {
            dataList.add(a);
        });

        Pager<List<AllocationData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Allocation successful");
        pagers.setContent(dataList);
        auditTrailService.saveAuditTrail("Claim", "Created remittance allocation ");
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/allocation/{id}")
    @PreAuthorize("hasAuthority('view_allocation')")
    public AllocationData getAllocation(@PathVariable(value = "id") Long id) {
        AllocationData allocation = allocationService.getAllocationByIdWithFailDetection(id).map();
        auditTrailService.saveAuditTrail("Claim", "Viewed remittance allocation identified by id "+id);
        return allocation;
    }

    @PutMapping("/allocation/{id}")
    @PreAuthorize("hasAuthority('edit_allocation')")
    public AllocationData updateRemitance(@PathVariable(value = "id") Long id, AllocationData allocationData) {
        AllocationData allocation = allocationService.updateAllocation(id, allocationData).map();
        auditTrailService.saveAuditTrail("Claim", "Edited remittance allocation identified by id "+id);
        return allocation;
    }

    @GetMapping("/allocation")
    @PreAuthorize("hasAuthority('view_allocation')")
    public ResponseEntity<?> getAllAllocations(
            @RequestParam(value = "invoiceNo", required = false) String invoiceNo,
            @RequestParam(value = "receiptNo", required = false) String receiptNo,
            @RequestParam(value = "payerId", required = false) Long payerId,
            @RequestParam(value = "schemeId", required = false) Long schemeId,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<AllocationData> list = allocationService.getAllocations(invoiceNo, receiptNo, receiptNo, payerId, schemeId, range, pageable)
                .map(remit -> remit.map());
         auditTrailService.saveAuditTrail("Claim", "Viewed all remittance allocation");
        Pager<List<AllocationData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Allocations");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }
}
