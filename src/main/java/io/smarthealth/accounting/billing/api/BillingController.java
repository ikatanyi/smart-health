package io.smarthealth.accounting.billing.api;

import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.accounting.billing.data.BillItemData;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillGroup;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
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
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api/v1")
public class BillingController {

    private final BillingService service;

    public BillingController(BillingService service) {
        this.service = service;
    }

    /*
    //api/v1/patient-billing    //create invoice and line items
    //api/v1/patient-billing/{billno}/line     
    
    //api/v1/visit/{visitid}/patient-billing
    
     */
    @PostMapping("/billing")
    public ResponseEntity<?> createPatientBill(@Valid @RequestBody BillData billData) {

        PatientBill patientbill = service.createPatientBill(billData);

        Pager<BillData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bill Successfully Created.");
        pagers.setContent(patientbill.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/billing/{id}")
    public BillData getPatientBill(@PathVariable(value = "id") Long code) {
        PatientBill bill = service.findOneWithNoFoundDetection(code);
        return bill.toData();
    }

    @PostMapping("/billing/{id}/items")
    public BillData addPatientBillItem(@PathVariable(value = "id") Long id, List<BillItemData> billLines) {
        PatientBill bill = service.findOneWithNoFoundDetection(id);
        return bill.toData();
    }

    @GetMapping("/billing")
    public ResponseEntity<?> getPatientBills(
            @RequestParam(value = "transactionNo", required = false) String transactionNo,
            @RequestParam(value = "visitNumber", required = false) String visitNumber,
            @RequestParam(value = "patientNumber", required = false) String patientNumber,
            @RequestParam(value = "paymentMode", required = false) String paymentMode,
            @RequestParam(value = "billNumber", required = false) String billNumber,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "status", required = false) BillStatus status,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<BillData> list = service.findAllBills(transactionNo, visitNumber, patientNumber, paymentMode, billNumber, status, range, pageable)
                .map(bill -> bill.toData());

        Pager<List<BillData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Patient Bills");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/bills")
    public ResponseEntity<?> listBillSummary(
            @RequestParam(value = "status", defaultValue = "Draft") BillStatus status) {
        List<PatientBillGroup> list = service.getPatientBillGroups(status);

        return ResponseEntity.ok(list);
    }
    @GetMapping("/bills/{visitId}/items")
    public ResponseEntity<?> listBillSummary(
            @PathVariable(value = "visitId") String visitId,
             @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {
         Pageable pageable = PaginationUtil.createPage(page, size);
        Page<PatientBillItem> list = service.getPatientBillItemByVisit(visitId, pageable);

       Pager<List<PatientBillItem>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Patient Bill Items");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }
}
