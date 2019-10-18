 package io.smarthealth.accounting.taxes.api;
 
import io.smarthealth.accounting.taxes.domain.Tax;
import io.smarthealth.accounting.taxes.service.TaxServices;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
@RestController
@Slf4j
@RequestMapping("/api")
public class TaxRestController {
    private final TaxServices service;

    public TaxRestController(TaxServices taxServices) {
        this.service = taxServices;
    }
    @PostMapping("/settings/taxes")
    public ResponseEntity<?> createTax(@Valid @RequestBody Tax tax) {
        
        Tax result = service.createTax(tax);
        
        Pager<Tax> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Tax Success Created");
        pagers.setContent(result); 
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
    @GetMapping("/settings/taxes/{id}")
    public ResponseEntity<?> getTax(@PathVariable(value = "id") Long code) {
        Tax tax = service.getTax(code)
                .orElseThrow(() -> APIException.notFound("Tax with id  {0} not found.", code));
        return ResponseEntity.ok(tax);
    }
    @GetMapping("/settings/taxes")
    public ResponseEntity<?> getAllTaxes( 
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<Tax> list = service.fetchAllTaxes(pageable); 
        Pager<List<Tax>> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details=new PageDetails();
        details.setPage(list.getNumber()+1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Taxes");
        pagers.setPageDetails(details);
         
        return ResponseEntity.ok(pagers);
    }
}
