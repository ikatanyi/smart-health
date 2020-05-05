/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.billing.api;

import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.accounting.billing.data.CopayData;
import io.smarthealth.accounting.billing.data.SummaryBill;
import io.smarthealth.accounting.billing.data.nue.BillDetail;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/api/v2")
public class BillingV2Controller {

    private final BillingService service;

    public BillingV2Controller(BillingService service) {
        this.service = service;
    }

    @PostMapping("/billing")
    public ResponseEntity<?> createBill(@Valid @RequestBody BillData billData) {

        PatientBill patientbill = service.createPatientBill(billData);

        Pager<BillData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bill Successfully Created.");
        pagers.setContent(patientbill.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/billing/{id}")
    public BillData getBill(@PathVariable(value = "id") Long code) {
        PatientBill bill = service.findOneWithNoFoundDetection(code);
        return bill.toData();
    }

    @PostMapping("/billing/{visitNumber}/copay")
    public ResponseEntity<?> createCopay(@PathVariable(value = "visitNumber") String visitNumber, @Valid @RequestBody CopayData data) {
        if (!visitNumber.equals(data.getVisitNumber())) {
            throw APIException.badRequest("Visit Number on path variable and body do not match");
        }
        data.setVisitStart(Boolean.FALSE);
        PatientBill patientbill = service.createCopay(data);
        if (patientbill == null) {
            throw APIException.badRequest("Copay creation was not successful");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(patientbill.toData());
    }

    @GetMapping("/billing")
    public ResponseEntity<?> getBills(
            @RequestParam(value = "visitNumber", required = false) String visitNumber,
            @RequestParam(value = "patientNumber", required = false) String patientNumber,
            @RequestParam(value = "hasBalance", required = false) Boolean hasBalance,
            @RequestParam(value = "isWalkin", required = false) Boolean isWakin,
            @RequestParam(value = "paymentMode", required = false) VisitEnum.PaymentMethod paymentMode,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        List<SummaryBill> list = service.getBillTotals(visitNumber, patientNumber, hasBalance, isWakin, paymentMode, range);

//        Pager<List<SummaryBill>> pagers = new Pager();
//        pagers.setCode("0");
//        pagers.setMessage("Success");
//        pagers.setContent(list.getContent());
//        PageDetails details = new PageDetails();
//        details.setPage(list.getNumber() + 1);
//        details.setPerPage(list.getSize());
//        details.setTotalElements(list.getTotalElements());
//        details.setTotalPage(list.getTotalPages());
//        details.setReportName("Patient Bills");
//        pagers.setPageDetails(details);
        return ResponseEntity.ok(list);
    }

    //get based on visit
    @GetMapping("/billing/{visitNumber}/items")
    public ResponseEntity<?> getBillDetails(
            @PathVariable(value = "visitNumber") String visitNumber,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        BillDetail details = service.getBillDetails(visitNumber, pageable);

//        Pager<List<SummaryBill>> pagers = new Pager();
//        pagers.setCode("0");
//        pagers.setMessage("Success");
//        pagers.setContent(list.getContent());
//        PageDetails details = new PageDetails();
//        details.setPage(list.getNumber() + 1);
//        details.setPerPage(list.getSize());
//        details.setTotalElements(list.getTotalElements());
//        details.setTotalPage(list.getTotalPages());
//        details.setReportName("Patient Bills");
//        pagers.setPageDetails(details);
        return ResponseEntity.ok(details);
    }

}
