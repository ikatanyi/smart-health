package io.smarthealth.accounting.old.api;

import io.smarthealth.accounting.old.data.CreatePayment;
import io.smarthealth.accounting.payment.data.MakePayment;
import io.smarthealth.accounting.old.data.FinancialTransactionData;
import io.smarthealth.accounting.old.domain.FinancialTransaction;
import io.smarthealth.accounting.old.service.PaymentOldService;
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
@Deprecated
public class PaymentControllerold {

    private final PaymentOldService service;

    public PaymentControllerold(PaymentOldService service) {
        this.service = service;
    }

    @PostMapping("/payments-deleted")
    public ResponseEntity<?> createPayment(@Valid @RequestBody CreatePayment transactionData) {

        FinancialTransaction trans = service.createTransaction(transactionData);

        Pager<FinancialTransactionData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment successfully Created.");
        pagers.setContent(trans.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
    
    @PostMapping("/payments-deleted/creditors")
    public ResponseEntity<?> createCreditorPayment(@Valid @RequestBody MakePayment creditorData) {

        FinancialTransaction trans = service.createTransaction(creditorData);

        Pager<FinancialTransactionData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment successfully Created.");
        pagers.setContent(trans.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/payments-deleted/{id}")
    public FinancialTransactionData getPayment(@PathVariable(value = "id") Long id) {
        FinancialTransaction trans = service.findTransactionOrThrowException(id);
        return trans.toData();
    }

    @PatchMapping("/payments-deleted/{id}")
    public FinancialTransactionData updatePayment(@PathVariable(value = "id") Long id, FinancialTransactionData transactionData) {
        FinancialTransactionData trans = service.updatePayment(id, transactionData);
        return trans;
    }

    @GetMapping("/payments-deleted")
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

    @PostMapping("/payments-deleted/{id}/emails")
    public String sendReceipt(@PathVariable(value = "id") Long id) {
        return service.emailReceipt(id);
    }

    @PostMapping("/payments-deleted/{id}/refunds")
    public ResponseEntity<?> refundPayment(@PathVariable(value = "id") Long id, @RequestParam(name = "amount") Double amount) {
        FinancialTransaction trans = service.refund(id, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(trans.toData());
    }

    /*Charging external payment gateways like M-pesa, Credit card */
    @PostMapping("/payments-deleted/charge")
    public ResponseEntity<?> charge(@RequestParam(name = "type") String type, FinancialTransactionData transaction) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment Gateway not implemented");
    }
    //TODO
    /*
      Provide mpesa integrations, credit cards,
     */
}
