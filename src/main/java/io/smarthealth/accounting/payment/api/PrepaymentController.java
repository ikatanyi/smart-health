/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.api;

import io.smarthealth.accounting.payment.data.CreatePrepayment;
import io.smarthealth.accounting.payment.data.PrepaymentData;
import io.smarthealth.accounting.payment.data.ReceiptData;
import io.smarthealth.accounting.payment.domain.Prepayment;
import io.smarthealth.accounting.payment.domain.Receipt;
import io.smarthealth.accounting.payment.service.PrepaymentService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
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
public class PrepaymentController {

    private final PrepaymentService service;

    public PrepaymentController(PrepaymentService service) {
        this.service = service;
    }

    @PostMapping("/prepayments")
//    @PreAuthorize("hasAuthority('create_receipt')")
    public ResponseEntity<Pager<PrepaymentData>> createPrepayment(@Valid @RequestBody CreatePrepayment deposit) {

        Prepayment payment = service.prepayments(deposit);
        Pager<PrepaymentData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment successfully Created.");
        pagers.setContent(payment.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/prepayments/{id}")
//    @PreAuthorize("hasAuthority('view_receipt')")
    public ResponseEntity<?> getPrepayment(@PathVariable(value = "id") Long id) {
        Prepayment payment = service.getPaymentOrThrow(id);
        return ResponseEntity.ok(payment.toData());
    }
    
    @GetMapping("/prepayments")
    @ResponseBody
//    @PreAuthorize("hasAuthority('view_receipt')")
    public ResponseEntity<Pager<List<PrepaymentData>>> getPaymentReceipts(
            @RequestParam(value = "patientNumber", required = false) final String patientNumber,
            @RequestParam(value = "receiptNo", required = false) final String receiptNo,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
             @RequestParam(value = "hasBalance", required = false) final Boolean hasBalance,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<PrepaymentData> list = service.getPayments(patientNumber, receiptNo, hasBalance, range, pageable)
                .map(x -> x.toData());

        Pager<List<PrepaymentData>> pagers = new Pager();
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
