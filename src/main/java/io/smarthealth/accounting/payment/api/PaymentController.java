package io.smarthealth.accounting.payment.api;

import io.smarthealth.accounting.payment.data.CreateTransactionData;
import io.smarthealth.accounting.payment.data.CreditorData;
import io.smarthealth.accounting.payment.data.FinancialTransactionData;
import io.smarthealth.accounting.payment.domain.FinancialTransaction;
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
    public ResponseEntity<?> createPayment(@Valid @RequestBody CreateTransactionData transactionData) {

        FinancialTransaction trans = service.createTransaction(transactionData);

        Pager<FinancialTransactionData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment successfully Created.");
        pagers.setContent(trans.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
    
    @PostMapping("/payments/creditors")
    public ResponseEntity<?> createCreditorPayment(@Valid @RequestBody CreditorData creditorData) {

        FinancialTransaction trans = service.createTransaction(creditorData);

        Pager<FinancialTransactionData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment successfully Created.");
        pagers.setContent(trans.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/payments/{id}")
    public FinancialTransactionData getPayment(@PathVariable(value = "id") Long id) {
        FinancialTransaction trans = service.findTransactionOrThrowException(id);
        return trans.toData();
    }

    @PatchMapping("/payments/{id}")
    public FinancialTransactionData updatePayment(@PathVariable(value = "id") Long id, FinancialTransactionData transactionData) {
        FinancialTransactionData trans = service.updatePayment(id, transactionData);
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
        Page<FinancialTransactionData> list = service.fetchTransactions(customer, invoice, receipt, pageable)
                .map(x -> x.toData());

        Pager<List<FinancialTransactionData>> pagers = new Pager();
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
        FinancialTransaction trans = service.refund(id, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(trans.toData());
    }

    /*Charging external payment gateways like M-pesa, Credit card */
    @PostMapping("/payments/charge")
    public ResponseEntity<?> charge(@RequestParam(name = "type") String type, FinancialTransactionData transaction) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment Gateway not implemented");
    }
    //TODO
    /*
      Provide mpesa integrations, credit cards,
     */
}
