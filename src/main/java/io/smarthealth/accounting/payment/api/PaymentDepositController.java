package io.smarthealth.accounting.payment.api;

import io.smarthealth.accounting.payment.data.PaymentDepositData;
import io.smarthealth.accounting.payment.domain.PaymentDeposit;
import io.smarthealth.accounting.payment.service.ReceivePaymentService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api/")
public class PaymentDepositController {

    private final ReceivePaymentService service;
    private final AuditTrailService auditTrailService;

    public PaymentDepositController(ReceivePaymentService service, AuditTrailService auditTrailService) {
        this.service = service;
        this.auditTrailService = auditTrailService;
    }
 
    @GetMapping("/payment-deposits/{id}")
//    @PreAuthorize("hasAuthority('view_receipt')")
    public ResponseEntity<?> getPaymentDeposit(@PathVariable(value = "id") Long id) {
        PaymentDeposit payment = service.getPaymentOrThrow(id);
        auditTrailService.saveAuditTrail("Deposits", "Searched deposit identified by "+id);
        return ResponseEntity.ok(payment.toData());
    }

    @GetMapping("/payment-deposits")
    @ResponseBody
//    @PreAuthorize("hasAuthority('view_receipt')")
    public ResponseEntity<Pager<List<PaymentDepositData>>> getPaymentDeposits(
            @RequestParam(value = "patientNumber", required = false) final String patientNumber,
            @RequestParam(value = "receiptNo", required = false) final String receiptNo,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "hasBalance", required = false) final Boolean hasBalance,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<PaymentDepositData> list = service.getPayments(patientNumber, receiptNo, hasBalance, range, pageable)
                .map(x -> x.toData());
        
        auditTrailService.saveAuditTrail("Deposits", "Viewed deposits Made");
        Pager<List<PaymentDepositData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Payments");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

}
