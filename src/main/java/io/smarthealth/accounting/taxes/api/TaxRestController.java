package io.smarthealth.accounting.taxes.api;

import io.smarthealth.accounting.taxes.domain.Tax;
import io.smarthealth.accounting.taxes.service.TaxServices;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequestMapping("/api")
public class TaxRestController {

    private final TaxServices service;

    public TaxRestController(TaxServices taxServices) {
        this.service = taxServices;
    }

    @PostMapping("/taxes")
    public ResponseEntity<?> createTax(@Valid @RequestBody Tax tax) {

        Tax result = service.createTax(tax);

        Pager<Tax> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Tax Success Created");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/taxes/{id}")
    public ResponseEntity<?> getTax(@PathVariable(value = "id") Long code) {
        Tax result = service.getTax(code);
        Pager<Tax> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Tax Success updated");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pagers);
    }

    @PutMapping("/taxes/{id}")
    public ResponseEntity<?> updatetax(@PathVariable(value = "id") Long id, Tax data) {
        Tax tax = service.updateTax(id, data);
        return ResponseEntity.ok(tax);
    }

    @GetMapping("/taxes")
    public ResponseEntity<?> getAllTaxes(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "1000") Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

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
