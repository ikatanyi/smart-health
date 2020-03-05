package io.smarthealth.clinical.pharmacy.api;

import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.clinical.pharmacy.data.DispensedDrugData;
import io.smarthealth.clinical.pharmacy.data.DrugRequest;
import io.smarthealth.clinical.pharmacy.domain.DispensedDrug;
import io.smarthealth.clinical.pharmacy.service.DispensingService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.inventory.data.TransData;
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
@RequestMapping("/api")
public class DispensingController {

    private final DispensingService service;

    public DispensingController(DispensingService service) {
        this.service = service;
    }

    @PostMapping("/pharmacybilling")
    public ResponseEntity<?> dispenseAndBilling(@Valid @RequestBody DrugRequest data) {

        String patientbill = service.dispense(data);

        Pager<TransData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bill Successfully Created.");
        pagers.setContent(new TransData(patientbill));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
    @PostMapping("/pharmacybilling/{}/return")
    public ResponseEntity<?> dispenseReturns(@Valid @RequestBody DrugRequest data) {

        String patientbill = service.dispense(data);

        Pager<TransData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bill Successfully Created.");
        pagers.setContent(new TransData(patientbill));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/pharmacybilling/{id}")
    public DispensedDrugData getDispensedDrug(@PathVariable(value = "id") Long code) {
        DispensedDrug bill = service.findDispensedDrugOrThrow(code);
        return bill.toData();
    }

    @GetMapping("/pharmacybilling")
    public ResponseEntity<?> getDispensedDrugs(
            @RequestParam(value = "transaction_id", required = false) String referenceNumber,
            @RequestParam(value = "visitNumber", required = false) String visitNumber,
            @RequestParam(value = "patientNumber", required = false) String patientNumber,
            @RequestParam(value = "prescriptionNo", required = false) String prescription,
            @RequestParam(value = "billNumber", required = false) String billNumber,
            @RequestParam(value = "status", required = false) BillStatus status,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<DispensedDrugData> list = service.findDispensedDrugs(referenceNumber, visitNumber, patientNumber, prescription, billNumber, status, pageable)
                .map(drug -> drug.toData());

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
}
