package io.smarthealth.debtor.claim.allocation.api;

import io.smarthealth.debtor.claim.allocation.data.AllocationData;
import io.smarthealth.debtor.claim.allocation.domain.Allocation;
import io.smarthealth.debtor.claim.allocation.service.AllocationService;
import io.smarthealth.debtor.claim.remittance.domain.RemittanceOld;
import io.smarthealth.debtor.claim.remittance.service.RemitanceService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final RemitanceService remitanceService;

    public AllocationController(AllocationService allocationService, RemitanceService remitanceService) {
        this.allocationService = allocationService;
        this.remitanceService = remitanceService;
    }

    @PostMapping("/allocation/{remmitanceId}")
    public ResponseEntity<?> createAllocation(@PathVariable("remmitanceId") final Long remmitanceId, @Valid @RequestBody List<AllocationData> allocationData) {
        RemittanceOld remitance = remitanceService.getRemitanceByIdWithFailDetection(remmitanceId);
        List<Allocation> allocatedAmount = allocationService.createAllocation(allocationData, remitance);
        List<AllocationData> dataList = new ArrayList<>();
        for (Allocation a : allocatedAmount) {
            AllocationData remittance = AllocationData.map(a);
            dataList.add(remittance);
        }

        Pager<List<AllocationData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Allocation successfully Created.");
        pagers.setContent(dataList);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/allocation/{id}")
    public AllocationData getAllocation(@PathVariable(value = "id") Long id) {
        AllocationData allocation = AllocationData.map(allocationService.getAllocationByIdWithFailDetection(id));
        return allocation;
    }

    @PutMapping("/allocation/{id}")
    public AllocationData updateRemitance(@PathVariable(value = "id") Long id, AllocationData allocationData) {
        AllocationData allocation = AllocationData.map(allocationService.updateAllocation(id, allocationData));
        return allocation;
    }

    @GetMapping("/allocation")
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
                .map(remit -> AllocationData.map(remit));

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
