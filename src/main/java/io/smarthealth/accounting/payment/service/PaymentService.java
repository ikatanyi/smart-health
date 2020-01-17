package io.smarthealth.accounting.payment.service;

import io.smarthealth.accounting.acc.service.AccountService;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.payment.data.PaymentData;
import io.smarthealth.accounting.payment.data.FinancialTransactionData;
import io.smarthealth.accounting.payment.data.CreateTransactionData;
import io.smarthealth.accounting.payment.domain.Payment;
import io.smarthealth.accounting.payment.domain.FinancialTransaction;
import io.smarthealth.accounting.payment.domain.enumeration.TrxType;
import io.smarthealth.accounting.payment.domain.specification.PaymentSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.accounting.payment.domain.FinancialTransactionRepository;
import io.smarthealth.infrastructure.utility.UuidGenerator;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final FinancialTransactionRepository transactionRepository;
    private final AccountService accountService;
    private final BillingService billingService;

    @Transactional
    public FinancialTransactionData createTransaction(CreateTransactionData transactionData) {

        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setDate(transactionData.getDate());
        transaction.setTrxType(TrxType.payment);
        transaction.setReceiptNo(RandomStringUtils.randomNumeric(6));
        transaction.setShiftNo("0000");
        transaction.setTransactionId(UuidGenerator.newUuid());
        transaction.setInvoice(transactionData.getBillNumber());

//        AccountEntity acc=accountService.findOneWithNotFoundDetection(transactionData.getAccount());
//        transaction.setAccount(acc);
        if (!transactionData.getPayment().isEmpty()) {
            List<Payment> paylist = transactionData.getPayment()
                    .stream()
                    .map(p -> createPayment(p))
                    .collect(Collectors.toList());
            transaction.addPayments(paylist);
        }

        final FinancialTransaction trans = transactionRepository.save(transaction);

        Optional<PatientBill> bill = billingService.findByBillNumber(transactionData.getBillNumber());

        if (bill.isPresent()) {
            PatientBill b = bill.get();
            b.setStatus(BillStatus.Final);
            billingService.save(b);
        }

        if (!transactionData.getBillItems().isEmpty()) {
            transactionData.getBillItems()
                    .stream()
                    .forEach(data -> {
                        PatientBillItem item = billingService.findBillItemById(data.getBillItemId());
                        item.setPaid(Boolean.TRUE);
                        item.setStatus(BillStatus.Final);
                        item.setBalance(0D);
                        billingService.updateBillItem(item);
                    });

        }

        return FinancialTransactionData.map(trans);

    }

    private Payment createPayment(PaymentData data) {
        Payment pay = new Payment();
        pay.setAmount(data.getAmount());
        pay.setMethod(data.getMethod());
        pay.setCurrency(data.getCurrency());
        pay.setReferenceCode(data.getReferenceCode());
        pay.setType(data.getType());
        return pay;
    }

    public FinancialTransactionData updatePayment(final Long id, FinancialTransactionData data) {
        FinancialTransaction trans = findTransactionOrThrowException(id);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Optional<FinancialTransaction> findById(final Long id) {
        return transactionRepository.findById(id);
    }

    public Page<FinancialTransaction> fetchTransactions(String customer, String invoice, String receipt, Pageable pageable) {
        Specification<FinancialTransaction> spec = PaymentSpecification.createSpecification(customer, invoice, receipt);
        Page<FinancialTransaction> transactions = transactionRepository.findAll(spec, pageable);
        return transactions;
    }

    public String emailReceipt(Long id) {
//        Transaction trans = findTransactionOrThrowException(id);
        //TODO Implement further
//        return "Receipt send to client "+trans.getReceiptNo();
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Transactional
    public FinancialTransactionData refund(Long id, Double amount) {

        FinancialTransaction trans = findTransactionOrThrowException(id);
        if (amount > trans.getAmount()) {
            throw APIException.badRequest("Refund amount is more than the receipt amount");
        }

        FinancialTransaction toSave = new FinancialTransaction();
        toSave.setDate(LocalDateTime.now());
        toSave.setTrxType(TrxType.refund);
        toSave.setReceiptNo(trans.getReceiptNo());
        toSave.setShiftNo("0000");
        toSave.setTransactionId(UUID.randomUUID().toString());
        toSave.setInvoice(trans.getInvoice());
        toSave.setAccount(trans.getAccount());
        toSave.setAmount(amount);

        FinancialTransaction trns = transactionRepository.save(toSave);
        return FinancialTransactionData.map(trns);

    }

    public FinancialTransaction findTransactionOrThrowException(Long id) {
        return findById(id)
                .orElseThrow(() -> APIException.notFound("Transaction with id {0} not found.", id));
    }

}
