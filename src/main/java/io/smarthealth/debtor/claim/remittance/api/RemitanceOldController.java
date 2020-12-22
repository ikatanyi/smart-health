package io.smarthealth.debtor.claim.remittance.api;

import io.smarthealth.debtor.claim.remittance.data.RemitanceData;
import io.smarthealth.debtor.claim.remittance.service.RemitanceService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
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
@Deprecated
//@Api
//@RestController
//@RequestMapping("/api/")
public class RemitanceOldController {

    private final RemitanceService remitanceservice;

    public RemitanceOldController(RemitanceService remitanceservice) {
        this.remitanceservice = remitanceservice;
    }

    @PostMapping("/remittance")
    public ResponseEntity<?> createRemittance(@Valid @RequestBody RemitanceData remitanceData) {

        RemitanceData remittance = RemitanceData.map(remitanceservice.createRemitance(remitanceData));

        Pager<RemitanceData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Remitance successfully Created.");
        pagers.setContent(remittance);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/remittance/{id}")
    public RemitanceData getRemittance(@PathVariable(value = "id") Long id) {
        RemitanceData remitance = RemitanceData.map(remitanceservice.getRemitanceByIdWithFailDetection(id));
        return remitance;
    }

    @PatchMapping("/remittance/{id}")
    public RemitanceData updateRemitance(@PathVariable(value = "id") Long id, RemitanceData remitanceData) {
        RemitanceData remitance = RemitanceData.map(remitanceservice.updateRemitance(id, remitanceData));
        return remitance;
    }

    @GetMapping("/remittance")
    public ResponseEntity<?> getRemittance(
            @RequestParam(value = "payerId", required = false) Long payerId,
            @RequestParam(value = "bankId", required = false) Long bankId,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "balance", required = false) Double balance,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<RemitanceData> list = remitanceservice.getRemitances(payerId, bankId, balance, range, pageable)
                .map(remit -> RemitanceData.map(remit));

        Pager<List<RemitanceData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Remitances");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }
}
