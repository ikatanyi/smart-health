package io.smarthealth.accounting.payment.api;

import io.smarthealth.accounting.payment.data.CopaymentData;
import io.smarthealth.accounting.payment.domain.Copayment;
import io.smarthealth.accounting.payment.service.CopaymentService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api/")
public class CopaymentController {

    private final CopaymentService service;

    public CopaymentController(CopaymentService service) {
        this.service = service;
    }

    @PostMapping("/copayment")
//    @PreAuthorize("hasAuthority('create_copayment')")
    public ResponseEntity<?> createCopayment(@Valid @RequestBody CopaymentData data) {
        Copayment copay = service.createCopayment(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(copay.toData());
    }

    @GetMapping("/copayment/{id}")
//    @PreAuthorize("hasAuthority('view_copayment')")
    public ResponseEntity<?> getCopayment(@PathVariable(value = "id") Long id) {
        Copayment copay = service.getCopaymentOrThrow(id);
        return ResponseEntity.ok(copay.toData());
    }

    @GetMapping("/copayment")
    @ResponseBody
//    @PreAuthorize("hasAuthority('view_copayment')")
    public ResponseEntity<?> getCopayments(
            @RequestParam(value = "visit_no", required = false) final String visitNumber,
            @RequestParam(value = "patient_no", required = false) final String patientNumber,
            @RequestParam(value = "receipt_no", required = false) final String receiptNo,
            @RequestParam(value = "paid", required = false) final Boolean paid,
            @RequestParam(value = "date_range", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<CopaymentData> list = service.getCopayments(visitNumber, patientNumber, visitNumber, receiptNo, paid, range, pageable)
                .map(x -> x.toData());

        Pager<List<CopaymentData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Copayments");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

}
