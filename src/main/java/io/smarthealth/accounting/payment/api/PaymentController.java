package io.smarthealth.accounting.payment.api;

import io.smarthealth.accounting.payment.data.TransactionData;
import io.smarthealth.accounting.payment.domain.Transaction;
import io.smarthealth.accounting.payment.service.PaymentService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
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
@RequestMapping("/api/")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping("/payments")
    public ResponseEntity<?> createPayment(@Valid @RequestBody TransactionData transactionData) {

        TransactionData trans = service.createTransaction(transactionData);

        Pager<TransactionData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment successfully Created.");
        pagers.setContent(trans);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/payments/{id}")
    public TransactionData getPayment(@PathVariable(value = "id") Long id) {
        Transaction trans = service.findTransactionOrThrowException(id);
        return TransactionData.map(trans);
    }

    @PatchMapping("/payments/{id}")
    public TransactionData updatePayment(@PathVariable(value = "id") Long id, TransactionData transactionData) {
        TransactionData trans = service.updatePayment(id, transactionData);
        return trans;
    }

    @GetMapping("/payments")
    public ResponseEntity<?> getPayments(
            @RequestParam(value = "customer", required = false) String customer,
            @RequestParam(value = "invoice", required = false) String invoice,
            @RequestParam(value = "receipt", required = false) String receipt,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<TransactionData> list = service.fetchTransactions(customer, invoice, receipt, pageable)
                .map(bill -> TransactionData.map(bill));

        Pager<List<TransactionData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Payment Transactions");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    @PostMapping("/payments/{id}/emails")
    public String sendReceipt(@PathVariable(value = "id") Long id) {
        return service.emailReceipt(id);
    }

    @PostMapping("/payments/{id}/refunds")
    public ResponseEntity<?> refundPayment(@PathVariable(value = "id") Long id, @RequestParam(name = "amount") Double amount) {
        TransactionData trans = service.refund(id, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(trans);
    }
    
    /*Charging external payment gateways like M-pesa, Credit card */
    @PostMapping("/payments/charge")
    public ResponseEntity<?> charge(@RequestParam(name = "type") String type, TransactionData transaction) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment Gateway not implemented");
    }
    //TODO
    /*
      Provide mpesa integrations, credit cards,
    */
}
