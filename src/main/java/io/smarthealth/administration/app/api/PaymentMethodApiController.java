package io.smarthealth.administration.app.api;

import io.smarthealth.administration.app.data.PaymentMethodData;
import io.smarthealth.administration.app.service.PaymentMethodService;
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
public class PaymentMethodApiController {

    private final PaymentMethodService service;

    public PaymentMethodApiController(PaymentMethodService service) {
        this.service = service;
    }

    @PostMapping("/payment-method")
    public ResponseEntity<?> createPaymentMethod(@Valid @RequestBody PaymentMethodData data) {

        PaymentMethodData result = service.createPaymentMethod(data);

        Pager<PaymentMethodData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment Mode Success Created");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/payment-method/{id}")
    public ResponseEntity<?> getPaymentMethod(@PathVariable(value = "id") Long id) {
        PaymentMethodData data = service.getPaymentMethod(id).toData();
        return ResponseEntity.ok(data);
    }

    @PutMapping("/payment-method/{id}")
    public ResponseEntity<?> updatePaymentMethod(@PathVariable(value = "id") Long id, PaymentMethodData data) {
        PaymentMethodData result = service.updatePaymentMethod(id, data);

        Pager<PaymentMethodData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Service Point Success updated");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pagers);
    }

    @GetMapping("/payment-method")
    public ResponseEntity<?> listPaymentMethods(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "1000", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<PaymentMethodData> list = service.listPaymentMethods(pageable);
        Pager<List<PaymentMethodData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Service points");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }
}
