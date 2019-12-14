package io.smarthealth.administration.app.api;

import io.smarthealth.administration.app.domain.PaymentTerms;
import io.smarthealth.administration.app.service.PaymentTermsService;
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
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kelsas
 */
@RestController
@Slf4j
@Api
@RequestMapping("/api/v1")
public class PaymentTermsRestController {

    private final PaymentTermsService service;

    public PaymentTermsRestController(PaymentTermsService paymentTerms) {
        this.service = paymentTerms;
    }

    @PostMapping("/payment-terms")
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

    @GetMapping("/payment-terms/{id}")
    public PaymentTerms getPaymentterm(@PathVariable(value = "id") Long id) {
        PaymentTerms paymentTerms = service.getPaymentTermByIdWithFailDetection(id);
        return paymentTerms;
    }

    @GetMapping("/payment-terms")
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

}
