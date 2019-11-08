package io.smarthealth.accounting.billing.api;

import io.smarthealth.accounting.billing.data.PatientBillData;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.service.PatientBillService;
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
@RequestMapping("/api/v1")
public class PatientBillingController {

    private final PatientBillService service; 

    public PatientBillingController(PatientBillService service) {
        this.service = service;
    }
 
    /*
    //api/v1/patient-billing    //create invoice and line items
    //api/v1/patient-billing/{billno}/line     
    
    //api/v1/visit/{visitid}/patient-billing
    
     */
    @PostMapping("/billing")
    public ResponseEntity<?> createPatientBill(@Valid @RequestBody PatientBillData patientBillData) {

        PatientBill patientbill = service.createPatientBill(patientBillData);

        Pager<PatientBillData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bill Successfully Created.");
        pagers.setContent(PatientBillData.map(patientbill));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
@GetMapping("/billing/{id}")
    public PatientBillData getPatientBill(@PathVariable(value = "id") Long code) {
        PatientBill bill = service.findOneWithNoFoundDetection(code);
        return PatientBillData.map(bill);
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
        Page<PatientBillData> list = service.findAllBills(referenceNumber,visitNumber,patientNumber,paymentMode,billNumber,status,pageable).map(bill -> PatientBillData.map(bill));

        Pager<List<PatientBillData>> pagers = new Pager();
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
