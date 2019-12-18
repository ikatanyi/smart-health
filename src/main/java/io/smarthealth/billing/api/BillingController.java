package io.smarthealth.billing.api;

import io.smarthealth.billing.data.BillData;
import io.smarthealth.billing.data.BillItemData;
import io.smarthealth.billing.domain.Bill;
import io.smarthealth.billing.service.BillingService;
import io.smarthealth.infrastructure.common.PaginationUtil;
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
    public ResponseEntity<?> createPatientBill(@Valid @RequestBody BillData patientBillData) {

        Bill patientbill = service.createPatientBill(patientBillData); 
         
        Pager<BillData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bill Successfully Created.");
        pagers.setContent(patientbill.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
@GetMapping("/billing/{id}")
    public BillData getPatientBill(@PathVariable(value = "id") Long code) {
        Bill bill = service.findOneWithNoFoundDetection(code);
        return bill.toData();
    }
    
    @PostMapping("/billing/{id}/items")
    public BillData addPatientBillItem(@PathVariable(value = "id") Long id,  List<BillItemData>billLines) {
        Bill bill = service.findOneWithNoFoundDetection(id);
        return bill.toData();
    }
    
    @GetMapping("/billing")
    public ResponseEntity<?> getPatientBills(
            @RequestParam(value = "referenceNumber", required = false) String referenceNumber,
            @RequestParam(value = "visitNumber", required = false) String visitNumber,
            @RequestParam(value = "patientNumber", required = false) String patientNumber,
            @RequestParam(value = "paymentMode", required = false) String paymentMode,
            @RequestParam(value = "billNumber", required = false) String billNumber,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {
         
        Pageable pageable = PaginationUtil.createPage(page, size); 
        Page<BillData> list = service.findAllBills(referenceNumber,visitNumber,patientNumber,paymentMode,billNumber,status,pageable)
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
    
}
