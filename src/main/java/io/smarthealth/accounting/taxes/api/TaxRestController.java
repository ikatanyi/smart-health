package io.smarthealth.accounting.taxes.api;

import io.smarthealth.accounting.taxes.domain.Tax;
import io.smarthealth.accounting.taxes.service.TaxServices;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequestMapping("/api")
public class TaxRestController {

    private final TaxServices service;
    private final AuditTrailService auditTrailService;

    public TaxRestController(TaxServices taxServices, AuditTrailService auditTrailService) {
        this.service = taxServices;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/taxes")
    @PreAuthorize("hasAuthority('create_tax')")
    public ResponseEntity<?> createTax(@Valid @RequestBody Tax tax) {

        Tax result = service.createTax(tax);

        Pager<Tax> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Tax Success Created");
        pagers.setContent(result);
        auditTrailService.saveAuditTrail("Tax", "Created tax item "+result.getTaxName());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/taxes/{id}")
    @PreAuthorize("hasAuthority('view_tax')")
    public ResponseEntity<?> getTax(@PathVariable(value = "id") Long code) {
        Tax result = service.getTax(code);
        Pager<Tax> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Tax Success updated");
        pagers.setContent(result);
        auditTrailService.saveAuditTrail("Tax", "Viewed tax item "+result.getTaxName());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pagers);
    }
    
    @GetMapping("/taxes/{name}/search")
    @PreAuthorize("hasAuthority('view_tax')")
    public ResponseEntity<?> getTaxesByName(@PathVariable(value = "name") String name) {
        List<Tax> result = service.fetchTaxesByName(name);
        auditTrailService.saveAuditTrail("Tax", "viewed tax items Identified by "+name);
        Pager<List<Tax>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(result);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pagers);
    }

    @PutMapping("/taxes/{id}")
    @PreAuthorize("hasAuthority('edit_tax')")
    public ResponseEntity<?> updatetax(@PathVariable(value = "id") Long id, @Valid @RequestBody Tax data) {
        Tax tax = service.updateTax(id, data);
        auditTrailService.saveAuditTrail("Tax", "Serched tax items Identified by "+id);
        return ResponseEntity.ok(tax);
    }

    @GetMapping("/taxes")
    @PreAuthorize("hasAuthority('view_tax')")
    public ResponseEntity<?> getAllTaxes(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "1000") Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        auditTrailService.saveAuditTrail("Tax", "viewed all taxes");
        Page<Tax> list = service.fetchAllTaxes(pageable);
        Pager<List<Tax>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Taxes");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }
}
