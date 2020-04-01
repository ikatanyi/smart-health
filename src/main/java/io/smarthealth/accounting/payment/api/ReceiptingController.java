package io.smarthealth.accounting.payment.api;

import io.smarthealth.accounting.payment.data.ReceiptData;
import io.smarthealth.accounting.payment.data.ReceivePayment;
import io.smarthealth.accounting.payment.domain.Receipt;
import io.smarthealth.accounting.payment.service.ReceivePaymentService;
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
import org.springframework.web.bind.annotation.PutMapping;
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
public class ReceiptingController {

    private final ReceivePaymentService service;

    public ReceiptingController(ReceivePaymentService service) {
        this.service = service;
    }

    @PostMapping("/receipting")
    public ResponseEntity<?> receivePayment(@Valid @RequestBody ReceivePayment paymentData) {

        Receipt payment = service.receivePayment(paymentData);
        Pager<ReceiptData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment successfully Created.");
        pagers.setContent(payment.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/receipting/{id}")
    public ResponseEntity<?> getPaymentReceipt(@PathVariable(value = "id") Long id) {
        Receipt payment = service.getPaymentOrThrow(id);
        return ResponseEntity.ok(payment.toData());
    }

    @PutMapping("/receipting/{receiptNo}/void")
    public ResponseEntity<?> voidPaymentReceipt(@PathVariable(value = "receiptNo") String receiptNo) {
        service.voidPayment(receiptNo);
        return ResponseEntity.accepted().build();
    }
//String payee, String receiptNo, String transactionNo, String shiftNo, String cashier, DateRange range, Pageable page

    @GetMapping("/receipting")
    @ResponseBody
//    @PreAuthorize("hasAuthority('view_payment')")
    public ResponseEntity<?> getPaymentReceipts(
            @RequestParam(value = "payer", required = false) final String payer,
            @RequestParam(value = "receipt_no", required = false) final String receiptNo,
            @RequestParam(value = "transaction_no", required = false) final String transactionNo,
            @RequestParam(value = "shift_no", required = false) final String shiftNo,
            @RequestParam(value = "cashier_id", required = false) final Long cashier,
            @RequestParam(value = "date_range", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<ReceiptData> list = service.getPayments(payer, receiptNo, transactionNo, shiftNo, cashier, range, pageable)
                .map(x -> x.toData());

        Pager<List<ReceiptData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Payments");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

}
