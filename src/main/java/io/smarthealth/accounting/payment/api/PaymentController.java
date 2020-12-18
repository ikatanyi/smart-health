package io.smarthealth.accounting.payment.api;

import io.smarthealth.accounting.payment.data.MakePayment;
import io.smarthealth.accounting.payment.data.MakePettyCashPayment;
import io.smarthealth.accounting.payment.data.PaymentData;
import io.smarthealth.accounting.payment.domain.Payment;
import io.smarthealth.accounting.payment.domain.enumeration.PayeeType;
import io.smarthealth.accounting.payment.service.MakePaymentService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class PaymentController {

    private final MakePaymentService service;
    private final AuditTrailService auditTrailService;
    
    public PaymentController(MakePaymentService service, AuditTrailService auditTrailService) {
        this.service = service;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/payments")
    @PreAuthorize("hasAuthority('create_payment')")
    public ResponseEntity<?> makePayment(@Valid @RequestBody MakePayment data) {

        Payment payment = service.makePayment(data);
        Pager<PaymentData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment successfully Created.");
        pagers.setContent(payment.toData());
        auditTrailService.saveAuditTrail("Payments", "Made a payment of "+payment.getAmount()+" to "+payment.getPayee());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/payments/{id}")
    @PreAuthorize("hasAuthority('view_payment')")
    public ResponseEntity<?> getPayment(@PathVariable(value = "id") Long id) {
        auditTrailService.saveAuditTrail("Payments", "Searched a payment identified by "+id);
        Payment payment = service.getPaymentOrThrow(id);        
        return ResponseEntity.ok(payment.toData());
    }

//     CreditorType creditorType, Long creditorId, String creditor, String transactionNo, DateRange range
    @GetMapping("/payments")
    @ResponseBody
    @PreAuthorize("hasAuthority('view_payment')")
    public ResponseEntity<?> getPayments(
            @RequestParam(value = "payee_type", required = false) final PayeeType creditorType,
            @RequestParam(value = "payee", required = false) final String creditor,
            @RequestParam(value = "payee_id", required = false) final Long creditorId,
            @RequestParam(value = "transaction_no", required = false) final String transactionNo,
            @RequestParam(value = "date_range", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<PaymentData> list = service.getPayments(creditorType, creditorId, creditor, transactionNo, range, pageable)
                .map(x -> x.toData());
        
        auditTrailService.saveAuditTrail("Payments", "Viewed all payments made");
        Pager<List<PaymentData>> pagers = new Pager();
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

    @PostMapping("/payments/pettycash")
    @PreAuthorize("hasAuthority('create_paymentPettyCash')")
    public ResponseEntity<?> makePayment(@Valid @RequestBody MakePettyCashPayment data) {

        Payment payment = service.makePayment(data);
        Pager<PaymentData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment successfully Created.");
        pagers.setContent(payment.toData());
        auditTrailService.saveAuditTrail("Payments", "Created a Petty cash payment");
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
}
