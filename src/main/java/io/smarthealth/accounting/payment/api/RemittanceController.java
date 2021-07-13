package io.smarthealth.accounting.payment.api;

import io.smarthealth.accounting.payment.data.CreateRemittance;
import io.smarthealth.accounting.payment.data.RemittanceData;
import io.smarthealth.accounting.payment.domain.Remittance;
import io.smarthealth.accounting.payment.service.RemittanceService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.accounting.payment.domain.Receipt;
import io.smarthealth.accounting.payment.data.ReceiptData;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;

import java.util.List;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api/")
public class RemittanceController {

    private final RemittanceService service;
    private final AuditTrailService auditTrailService;

    public RemittanceController(RemittanceService service, AuditTrailService auditTrailService) {
        this.service = service;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/remittance")
    @PreAuthorize("hasAuthority('create_remittance')")
    public ResponseEntity<?> receivePayment(@Valid @RequestBody CreateRemittance data) {

        Receipt remittance = service.createRemittance(data);

        Pager<ReceiptData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Remittance Successfully Created.");
        pagers.setContent(remittance.toData());
        auditTrailService.saveAuditTrail("Remmitance", "Received Payement from " + remittance.getReceivedFrom() + " receiptNo " + remittance.getReceiptNo());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/remittance/{id}")
    @PreAuthorize("hasAuthority('view_remittance')")
    public RemittanceData getRemittance(@PathVariable(value = "id") Long id) {
        Remittance remitance = service.getRemittanceOrThrow(id);
        auditTrailService.saveAuditTrail("Remmitance", "Searched Remmitance Payement Identified by " + id);
        return remitance.toData();
    }
//Long payerId, String receipt, String remittanceNo, Boolean hasBalance, DateRange range, Pageable page

    @GetMapping("/remittance")
    @PreAuthorize("hasAuthority('view_remittance')")
    public ResponseEntity<?> getRemittance(
            @RequestParam(value = "payerId", required = false) Long payerId,
            @RequestParam(value = "receipt", required = false) Long receipt,
            @RequestParam(value = "remittanceNo", required = false) Long remittanceNo,
            @RequestParam(value = "hasBalance", required = false) Boolean hasBalance,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<RemittanceData> list = service.getRemittances(payerId, dateRange, dateRange, hasBalance, range, pageable)
                .map(x -> x.toData());

        auditTrailService.saveAuditTrail("Remmitance", "Viewed all Remmitance Payements ");
        Pager<List<RemittanceData>> pagers = new Pager();
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
