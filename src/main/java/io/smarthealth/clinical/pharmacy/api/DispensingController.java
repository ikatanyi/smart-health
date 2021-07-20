package io.smarthealth.clinical.pharmacy.api;

import io.smarthealth.clinical.pharmacy.data.DispensedDrugData;
import io.smarthealth.clinical.pharmacy.data.DrugRequest;
import io.smarthealth.clinical.pharmacy.data.ReturnedDrugData;
import io.smarthealth.clinical.pharmacy.domain.DispensedDrug;
import io.smarthealth.clinical.pharmacy.service.DispensingService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.inventory.data.TransData;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class DispensingController {

    private final DispensingService service;
    private final AuditTrailService auditTrailService;

    public DispensingController(DispensingService service, AuditTrailService auditTrailService) {
        this.service = service;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/pharmacybilling")
    @PreAuthorize("hasAuthority('create_dispense')")
    public ResponseEntity<?> dispenseAndBilling(@Valid @RequestBody DrugRequest data) {

        String patientbill = service.dispense(data);
        auditTrailService.saveAuditTrail("Pharmacy", "Dispensed and billed drugs for patient " + data.getPatientName());
        Pager<TransData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bill Successfully Created.");
        pagers.setContent(new TransData(patientbill));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @PostMapping("/pharmacybilling/return")
    @PreAuthorize("hasAuthority('create_dispense')")
    public ResponseEntity<?> dispenseReturns(@Valid @RequestBody DrugRequest data) {

        String patientbill = service.dispense(data);
        auditTrailService.saveAuditTrail("Pharmacy", "Accepted drugs return for patient " + data.getPatientName());
        Pager<TransData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bill Successfully Created.");
        pagers.setContent(new TransData(patientbill));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/pharmacybilling/{id}")
    @PreAuthorize("hasAuthority('view_dispense')")
    public DispensedDrugData getDispensedDrug(@PathVariable(value = "id") Long code) {
        DispensedDrug bill = service.findDispensedDrugOrThrow(code);
        auditTrailService.saveAuditTrail("Pharmacy", "Searched dispensed drugs identified by id " + code);
        return bill.toData();
    }

    @PutMapping("/pharmacybilling/{requestId}/status")
    @PreAuthorize("hasAuthority('edit_dispense')")
    public ResponseEntity<?> updatePatientDrug(@PathVariable(value = "requestId") Long id) {
        Boolean status = service.UpdateFullfillerStatus(id);
        auditTrailService.saveAuditTrail("Pharmacy", "Fulfilled  drug dispensation identified by id " + id);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/pharmacybilling")
    @PreAuthorize("hasAuthority('view_dispense')")
    public ResponseEntity<?> getDispensedDrugs(
            @RequestParam(value = "transaction_id", required = false) String referenceNumber,
            @RequestParam(value = "visitNumber", required = false) String visitNumber,
            @RequestParam(value = "patientNumber", required = false) String patientNumber,
            @RequestParam(value = "prescriptionNo", required = false) String prescription,
            @RequestParam(value = "billNumber", required = false) String billNumber,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "isReturn", required = false) Boolean isReturn,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Pageable pageable = PaginationUtil.createPage(page, size, Sort.by(Sort.Direction.DESC, "dispensedDate"));
        Page<DispensedDrugData> list = service.findDispensedDrugs(referenceNumber, visitNumber, patientNumber, prescription,
                billNumber, isReturn, range, pageable)
                .map(drug -> drug.toData());
        auditTrailService.saveAuditTrail("Pharmacy", "Viewed drug dispensation history");
        Pager<List<DispensedDrugData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Patient Dispensed drugs");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    ///returned drugs
    @PostMapping("/pharmacybilling/{visitNumber}/returns")
    @PreAuthorize("hasAuthority('create_dispense')")
    public ResponseEntity<?> DrugsReturn(@PathVariable String visitNumber, @Valid @RequestBody List<ReturnedDrugData> data) {

        List<DispensedDrugData> returned = service.returnItems(visitNumber, data)
                .stream()
                .map((returnItem) -> returnItem.toData())
                .collect(Collectors.toList());
        auditTrailService.saveAuditTrail("Pharmacy", "Viewed drug returns for visit " + visitNumber);
        Pager<List<DispensedDrugData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Drugs Successfully returned");
        pagers.setContent(returned);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
    //TODO:: dispensing 
}
