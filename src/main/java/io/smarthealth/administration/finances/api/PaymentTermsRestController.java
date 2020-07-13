package io.smarthealth.administration.finances.api;

import io.smarthealth.administration.finances.domain.PaymentTerms;
import io.smarthealth.administration.finances.service.PaymentTermsService;
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
public class PaymentTermsRestController {

    private final PaymentTermsService service;

    public PaymentTermsRestController(PaymentTermsService paymentTerms) {
        this.service = paymentTerms;
    }

    @PostMapping("/payment-terms")
    @PreAuthorize("hasAuthority('create_paymentTerms')")
    public ResponseEntity<?> createPaymentTerms(@Valid @RequestBody PaymentTerms paymentTerms) {
        if (service.getPaymentTermByName(paymentTerms.getTermsName()).isPresent()) {
            throw APIException.conflict("Payment Terms with name {0} already exists.", paymentTerms.getTermsName());
        }

        PaymentTerms result = service.createPaymentTerm(paymentTerms);

        Pager<PaymentTerms> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment Terms created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }
    
    @PutMapping("/payment-terms/{id}")
    @PreAuthorize("hasAuthority('create_paymentTerms')")
    public ResponseEntity<?> upatePaymentTerms(@PathVariable("id") Long id, @Valid @RequestBody PaymentTerms paymentTerms) {
        PaymentTerms result = service.updatePaymentTerm(id, paymentTerms);

        Pager<PaymentTerms> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment Terms updated successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/payment-terms/{id}")
    @PreAuthorize("hasAuthority('view_paymentTerms')")
    public PaymentTerms getPaymentterm(@PathVariable(value = "id") Long id) {
        PaymentTerms paymentTerms = service.getPaymentTermByIdWithFailDetection(id);
        return paymentTerms;
    }

    @GetMapping("/payment-terms")
    @PreAuthorize("hasAuthority('view_paymentTerms')")
    public ResponseEntity<?> getAllPaymentTerms(
            @RequestParam(value = "includeClosed", required = false, defaultValue = "false") final boolean includeClosed,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<PaymentTerms> list = service.getPaymentTerms(pageable, includeClosed);

        Pager<List<PaymentTerms>> pagers = new Pager();
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
    
    @GetMapping("/payment-terms/{name}/search")
    @PreAuthorize("hasAuthority('view_paymentTerms')")
    public ResponseEntity<?> getPaymentTermsByName(@PathVariable("name") String name) {
        List<PaymentTerms> list = service.getPaymentTermsByName(name);

        Pager<List<PaymentTerms>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        PageDetails details = new PageDetails();
        details.setReportName("Payment Terms");
        pagers.setContent(list);
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

}
