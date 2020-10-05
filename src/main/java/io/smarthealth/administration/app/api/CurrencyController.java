package io.smarthealth.administration.app.api;

import io.smarthealth.administration.app.domain.Currency;
import io.smarthealth.administration.app.service.CurrencyService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kelsas
 */
@RestController
@Slf4j
@Api
@RequestMapping("/api")
public class CurrencyController {

    private final CurrencyService service;

    public CurrencyController(CurrencyService currencyService) {
        this.service = currencyService;
    }

    @PostMapping("/currencies")
    @PreAuthorize("hasAuthority('create_currencies')")
    public ResponseEntity<?> createCurrency(@Valid @RequestBody Currency currency) {
        if (service.getCurrencyByName(currency.getName()).isPresent()) {
            throw APIException.conflict("Currency with name {0} already exists.", currency.getName());
        }

        Currency result = service.createCurrency(currency);

        Pager<Currency> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Currency created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }
    
    @PutMapping("/currencies/{id}")
    @PreAuthorize("hasAuthority('create_currencies')")
    public ResponseEntity<?> updateCurrency(@PathVariable(value = "id") Long id, @Valid @RequestBody Currency currency) {
        
        Currency result = service.updateCurrency(id, currency);

        Pager<Currency> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Currency created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/currencies/{id}")
//    @PreAuthorize("hasAuthority('view_currencies')")
    public Currency getCurrency(@PathVariable(value = "id") Long code) {
        Currency currencyService = service.getCurrency(code)
                .orElseThrow(() -> APIException.notFound("Payment Terms with id {0} not found.", code));
        return currencyService;
    }
    
    @GetMapping("/currencies/{term}/search")
    @PreAuthorize("hasAuthority('view_currencies')")
    public ResponseEntity<?> serachCurrency(@PathVariable(value = "term") String term) {
        List<Currency>currencies =  service.getCurrencyByNameOrCode(term);
        Pager<List<Currency>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(currencies);
        PageDetails details = new PageDetails();
        details.setReportName("Currencies");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);

    }

    @GetMapping("/currencies")
    @PreAuthorize("hasAuthority('view_currencies')")
    public ResponseEntity<?> getAllCurrency(
            @RequestParam(value = "includeClosed", required = false, defaultValue = "false") final boolean includeClosed,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<Currency> list = service.getCurrency(pageable, includeClosed);

        Pager<List<Currency>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Payment Terms");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

}
