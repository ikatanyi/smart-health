package io.smarthealth.accounting.billing.api;

import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.accounting.billing.data.BillItemData;
import io.smarthealth.accounting.billing.data.CopayData;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.data.SummaryBill;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import net.bytebuddy.asm.Advice;
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
public class BillingController {

    private final BillingService service; 

    public BillingController(BillingService service) {
        this.service = service;
    }
    
 
    @PostMapping("/billing")
    @PreAuthorize("hasAuthority('create_patientBill')") 
    public ResponseEntity<?> createPatientBill(@Valid @RequestBody BillData billData) {
       
        PatientBill patientbill = service.createPatientBill(billData);

        Pager<BillData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bill Successfully Created.");
        pagers.setContent(patientbill.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
     
    @GetMapping("/billing/{id}")
    @PreAuthorize("hasAuthority('view_patientBill')") 
    public BillData getPatientBill(@PathVariable(value = "id") Long code) {
        PatientBill bill = service.findOneWithNoFoundDetection(code);
        return bill.toData();
    }

    @PostMapping("/billing/{id}/items")
    @PreAuthorize("hasAuthority('create_patientBill')") 
    public BillData addPatientBillItem(@PathVariable(value = "id") Long id, List<BillItemData> billLines) {
        PatientBill bill = service.findOneWithNoFoundDetection(id);
        return bill.toData();
    }
//    @PostMapping("/billing/{visitId}/cancel")
//    @PreAuthorize("hasAuthority('create_patientBill')") 
//    public BillData addPatientBillItem(@PathVariable(value = "visitId") Long id, List<BillItemData> billLines) {
//        PatientBill bill = service.findOneWithNoFoundDetection(id);
//        return bill.toData();
//    }
//  String patientNo, String visitNo, Boolean hasBalance, DateRange range
//String visitNumber, String patientNumber, Boolean hasBalance, DateRange range

    @GetMapping("/billing/summary")
    @PreAuthorize("hasAuthority('view_patientBill')") 
    public ResponseEntity<?> getPatientBillSummary(
            @RequestParam(value = "visitNumber", required = false) String visitNumber,
            @RequestParam(value = "patientNumber", required = false) String patientNumber,
            @RequestParam(value = "hasBalance", required = false) Boolean hasBalance,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Page<SummaryBill> list = service.getSummaryBill(visitNumber, patientNumber, hasBalance, range, pageable);

        Pager<List<SummaryBill>> pagers = new Pager();
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
    
   @PostMapping("/billing/{visitNumber}/copay")
   @PreAuthorize("hasAuthority('create_patientBill')") 
    public ResponseEntity<?> createCopayBill(@PathVariable(value = "visitNumber") String visitNumber,@Valid @RequestBody  CopayData data) {
        if(!visitNumber.equals(data.getVisitNumber())){
            throw APIException.badRequest("Visit Number on path variable and body do not match");
        }
        data.setVisitStart(Boolean.FALSE);
            PatientBill patientbill = service.createCopay(data);
            if(patientbill ==null){
                throw APIException.badRequest("Copay creation was not successful");
            }
 
        return ResponseEntity.status(HttpStatus.CREATED).body(patientbill.toData());
    }
    @GetMapping("/billing/items")
    @PreAuthorize("hasAuthority('view_patientBill')") 
    public ResponseEntity<?> getPatientBillItems(
            @RequestParam(value = "patientNumber", required = false) String patientNo,
            @RequestParam(value = "visitNumber", required = false) String visitNo,
            @RequestParam(value = "billNumber", required = false) String billNumber,
            @RequestParam(value = "transactionNo", required = false) String transactionId,
            @RequestParam(value = "servicepointId", required = false) Long servicePointId,
            @RequestParam(value = "hasBalance", required = false) Boolean hasBalance,
            @RequestParam(value = "status", required = false) BillStatus status,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Page<BillItemData> list = service.getPatientBillItems(patientNo, visitNo, billNumber, transactionId, servicePointId, hasBalance, status, range, pageable)
                .map(x -> x.toData());

        Pager<List<BillItemData>> pagers = new Pager();
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

    @GetMapping("/billing/items/{visitNumber}/receipted")
    @PreAuthorize("hasAuthority('view_patientBill')") 
    public ResponseEntity<?> getReceiptedBillItems(
            @PathVariable(value = "visitNumber", required = false) String visitNo,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<BillItemData> list = service.getReceiptedBillItems(visitNo, pageable)
                .map(x -> x.toData());

        Pager<List<BillItemData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Patient Invoice Receipted Items");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/billing/walkin")
    @PreAuthorize("hasAuthority('view_patientBill')") 
    public ResponseEntity<?> getWalkinBillSummary(
            @RequestParam(value = "walkinNumber", required = false) String walkinNumber,
            @RequestParam(value = "hasBalance", required = false) Boolean hasBalance,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
//        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Page<SummaryBill> list = service.getWalkinSummaryBill(walkinNumber, hasBalance, pageable);

        Pager<List<SummaryBill>> pagers = new Pager();
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

    @GetMapping("/billing/walkin/items")
    @PreAuthorize("hasAuthority('view_patientBill')") 
    public ResponseEntity<?> getWalkinBillItems(
            @RequestParam(value = "walkinNo", required = false) String walkinNo,
            @RequestParam(value = "hasBalance", required = false) Boolean hasBalance,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<BillItemData> list = service.getWalkBillItems(walkinNo, hasBalance, pageable)
                .map(x -> x.toData());

        Pager<List<BillItemData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Walkin Patient Bills");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

//    @GetMapping("/bills")
//    public ResponseEntity<?> listBillSummary(
//            @RequestParam(value = "status", defaultValue = "Draft") BillStatus status) {
//        List<PatientBillGroup> list = service.getPatientBillGroups(status);
//
//        return ResponseEntity.ok(list);
//    }
//
//    @GetMapping("/bills/{visitId}/items")
//    public ResponseEntity<?> listBillSummary(
//            @PathVariable(value = "visitId") String visitId,
//            @RequestParam(value = "page", required = false) Integer page,
//            @RequestParam(value = "pageSize", required = false) Integer size) {
//        Pageable pageable = PaginationUtil.createPage(page, size);
//        Page<PatientBillItem> list = service.getPatientBillItemByVisit(visitId, pageable);
//
//        Pager<List<PatientBillItem>> pagers = new Pager();
//        pagers.setCode("0");
//        pagers.setMessage("Success");
//        pagers.setContent(list.getContent());
//        PageDetails details = new PageDetails();
//        details.setPage(list.getNumber() + 1);
//        details.setPerPage(list.getSize());
//        details.setTotalElements(list.getTotalElements());
//        details.setTotalPage(list.getTotalPages());
//        details.setReportName("Patient Bill Items");
//        pagers.setPageDetails(details);
//        return ResponseEntity.ok(pagers);
//    } 
    //TODO bill cancellations
    //Changes status of the bills 
  
}
