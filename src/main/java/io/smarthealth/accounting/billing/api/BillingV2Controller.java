package io.smarthealth.accounting.billing.api;

import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.accounting.billing.data.BillItemData;
import io.smarthealth.accounting.billing.data.CopayData;
import io.smarthealth.accounting.billing.data.PatientBalance;
import io.smarthealth.accounting.billing.data.SummaryBill;
import io.smarthealth.accounting.billing.data.VoidBillItem;
import io.smarthealth.accounting.billing.data.nue.BillDetail;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
public class BillingV2Controller {

    private final BillingService service;
    private final AuditTrailService auditTrailService;

//    public BillingV2Controller(BillingService service) {
//        this.service = service;
//    }

    @PostMapping("/billing")
    @PreAuthorize("hasAuthority('create_billV2')")
    public ResponseEntity<?> createBill(@Valid @RequestBody BillData billData) {

        PatientBill patientbill = service.createPatientBill(billData);

        Pager<BillData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bill Successfully Created.");
        pagers.setContent(patientbill.toData());
        auditTrailService.saveAuditTrail("Billing", "Created a bill for "+patientbill.getPatient().getFullName());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/billing/{id}")
    @PreAuthorize("hasAuthority('view_billV2')")
    public BillData getBill(@PathVariable(value = "id") Long code) {
        PatientBill bill = service.findOneWithNoFoundDetection(code);
        auditTrailService.saveAuditTrail("Billing", "Viewed a bill for "+bill.getPatient().getFullName());
        return bill.toData();
    }

    @PostMapping("/billing/{visitNumber}/copay")
    @PreAuthorize("hasAuthority('create_billV2')")
    public ResponseEntity<?> createCopay(@PathVariable(value = "visitNumber") String visitNumber, @Valid @RequestBody CopayData data) {
        if (!visitNumber.equals(data.getVisitNumber())) {
            throw APIException.badRequest("Visit Number on path variable and body do not match");
        }
        data.setVisitStart(Boolean.FALSE);
        PatientBill patientbill = service.createCopay(data);
        if (patientbill == null) {
            throw APIException.badRequest("Copay creation was not successful");
        }
        auditTrailService.saveAuditTrail("Billing", "Created a copay bill for "+patientbill.getPatient().getFullName());
        return ResponseEntity.status(HttpStatus.CREATED).body(patientbill.toData());
    }

    @GetMapping("/billing")
    @PreAuthorize("hasAuthority('view_billV2')")
    public ResponseEntity<?> getBills(
            @RequestParam(value = "visitNumber", required = false) String visitNumber,
            @RequestParam(value = "patientNumber", required = false) String patientNumber,
            @RequestParam(value = "hasBalance", required = false) Boolean hasBalance,
            @RequestParam(value = "isWalkin", required = false) Boolean isWakin,
            @RequestParam(value = "paymentMode", required = false) PaymentMethod paymentMode,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "includeCanceled", required = false, defaultValue = "false") final boolean includeCanceled,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        List<SummaryBill> list = service.getBillTotals(visitNumber, patientNumber, hasBalance, isWakin, paymentMode, range, includeCanceled);

        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        Page<SummaryBill> pages = new PageImpl<>(list.subList(start, end), pageable, list.size());
        auditTrailService.saveAuditTrail("Billing", "Viewed a bill summary ");
        Pager<List<SummaryBill>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(pages.getContent());
        PageDetails details = new PageDetails();
        details.setPage(pages.getNumber() + 1);
        details.setPerPage(pages.getSize());
        details.setTotalElements(pages.getTotalElements());
        details.setTotalPage(pages.getTotalPages());
        details.setReportName("Patient Bills");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    //get based on visit
    @GetMapping("/billing/{visitNumber}/items")
    @PreAuthorize("hasAuthority('view_billV2')")
    public ResponseEntity<?> getBillDetails(
            @PathVariable(value = "visitNumber") String visitNumber,
            @RequestParam(value = "billPayMode" , required = false) PaymentMethod paymentMethod,
            @RequestParam(value = "finalized", required = false, defaultValue = "false") final boolean finalized,
            @RequestParam(value = "includeCanceled", required = false, defaultValue = "false") final boolean includeCanceled,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createUnPaged(page, size);
        BillDetail details = service.getBillDetails(visitNumber, includeCanceled,paymentMethod, pageable);
        auditTrailService.saveAuditTrail("Billing", "Viewed Billed items");
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

    @PutMapping("/billing/{visitNumber}/void")
    @PreAuthorize("hasAuthority('edit_billV2')")
    public ResponseEntity<?> cancelBills(@PathVariable(value = "visitNumber") String visitNumber, @Valid @RequestBody List<VoidBillItem> billItems) {
        List<BillItemData> bills = service.voidBillItem(visitNumber, billItems).stream().map(x -> x.toData()).collect(Collectors.toList());
        auditTrailService.saveAuditTrail("Billing", "Deleted a bill items for visit. "+visitNumber);
        return ResponseEntity.ok(bills);
    }
    
     @GetMapping("/billing/balance")
    public ResponseEntity<PatientBalance> getBalance(
            @RequestParam(value = "visitNumber", required = false) String visitNumber,
            @RequestParam(value = "patientNumber", required = false) String patientNumber) {
        return ResponseEntity.ok(new PatientBalance());
    }

    Page<SummaryBill> toPage(List<SummaryBill> list, int pagesize, int pageNo) {

        int totalpages = list.size() / pagesize;
        PageRequest pageable = PageRequest.of(pageNo, pagesize);

        int max = pageNo >= totalpages ? list.size() : pagesize * (pageNo + 1);
        int min = pageNo > totalpages ? max : pagesize * pageNo;

        log.info("totalpages{} pagesize {} pageNo {}   list size {} min {}   max {} ...........", totalpages, pagesize, pageNo, list.size(), min, max);
        Page<SummaryBill> pageResponse = new PageImpl<>(list.subList(min, max), pageable, list.size());
        return pageResponse;
    }
}
