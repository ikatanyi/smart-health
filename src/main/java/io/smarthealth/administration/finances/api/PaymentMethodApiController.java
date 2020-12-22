package io.smarthealth.administration.finances.api;

import io.smarthealth.administration.finances.data.PaymentMethodData;
import io.smarthealth.administration.finances.service.PaymentMethodService;
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
public class PaymentMethodApiController {

    private final PaymentMethodService service;
    private final AuditTrailService auditTrailService; 

    public PaymentMethodApiController(PaymentMethodService service, AuditTrailService auditTrailService) {
        this.service = service;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/payment-method")
    @PreAuthorize("hasAuthority('create_paymentMethod')")
    public ResponseEntity<?> createPaymentMethod(@Valid @RequestBody PaymentMethodData data) {

        PaymentMethodData result = service.createPaymentMethod(data);
        auditTrailService.saveAuditTrail("Administration", "Created Payment method "+result.getName());
        Pager<PaymentMethodData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment Mode Success Created");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/payment-method/{id}")
    @PreAuthorize("hasAuthority('view_paymentMethod')")
    public ResponseEntity<?> getPaymentMethod(@PathVariable(value = "id") Long id) {
        PaymentMethodData data = service.getPaymentMethod(id).toData();
        auditTrailService.saveAuditTrail("Administration", "Viewed Payment method "+data.getName());
        return ResponseEntity.ok(data);
    }

    @PutMapping("/payment-method/{id}")
     @PreAuthorize("hasAuthority('edit_paymentMethod')")
    public ResponseEntity<?> updatePaymentMethod(@PathVariable(value = "id") Long id, @Valid @RequestBody PaymentMethodData data) {
        PaymentMethodData result = service.updatePaymentMethod(id, data);
        auditTrailService.saveAuditTrail("Administration", "Edited Payment method "+result.getName());
        Pager<PaymentMethodData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Service Point Success updated");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pagers);
    }

    @GetMapping("/payment-method")
    @PreAuthorize("hasAuthority('view_paymentMethod')")
    public ResponseEntity<?> listPaymentMethods(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "1000", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        auditTrailService.saveAuditTrail("Administration", "Viewed all Payment methods ");
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
        details.setReportName("Payment Methods");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }
    
    @GetMapping("/payment-method/{name}/search")
    @PreAuthorize("hasAuthority('view_paymentMethod')")
    public ResponseEntity<?> getPaymentMethodByName(@PathVariable("name") String name) {

        List<PaymentMethodData> list = service.getPaymentMethodsByName(name);
        Pager<List<PaymentMethodData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list);
        PageDetails details = new PageDetails();
        details.setReportName("Payment Methods");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }
}
