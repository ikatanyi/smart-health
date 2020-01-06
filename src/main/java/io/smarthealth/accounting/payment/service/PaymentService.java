package io.smarthealth.accounting.payment.service;

import io.smarthealth.accounting.payment.data.TransactionData;
import io.smarthealth.accounting.payment.domain.Transaction;
import io.smarthealth.accounting.payment.domain.TransactionRepository;
import io.smarthealth.accounting.payment.domain.TranxType;
import io.smarthealth.accounting.payment.domain.specification.PaymentSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Service
public class PaymentService {

    private final TransactionRepository transactionRepository;

    public PaymentService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionData createTransaction(TransactionData transactionData) {
        Transaction transaction = new Transaction();
        transaction.setPayer(transactionData.getPayer());
        transaction.setInvoice(transactionData.getInvoice());
        transaction.setReceiptNo(UUID.randomUUID().toString());
        transaction.setCreditNote(transactionData.getCreditNote());
        transaction.setDate(transactionData.getDate());
        transaction.setType(transactionData.getType());
        transaction.setMethod(transactionData.getMethod());
        transaction.setStatus(transactionData.getStatus());
        transaction.setCurrency(transactionData.getCurrency());
        transaction.setAmount(transactionData.getAmount());
        transaction.setNotes(transactionData.getNotes());
        transaction.setShiftNo("0000");

        Transaction trans = transactionRepository.save(transaction);

        return TransactionData.map(trans);

    }

    public TransactionData updatePayment(final Long id, TransactionData data) {
        Transaction trans = findTransactionOrThrowException(id);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Optional<Transaction> findById(final Long id) {
        return transactionRepository.findById(id);
    }

    public Page<Transaction> fetchTransactions(String customer, String invoice, String receipt, Pageable pageable) {
        Specification<Transaction> spec = PaymentSpecification.createSpecification(customer, invoice, receipt);
        Page<Transaction> transactions = transactionRepository.findAll(spec, pageable);
        return transactions;
    }

    public String emailReceipt(Long id) {
//        Transaction trans = findTransactionOrThrowException(id);
        //TODO Implement further
//        return "Receipt send to client "+trans.getReceiptNo();
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Transactional
    public TransactionData refund(Long id, Double amount) {
        Transaction trans = findTransactionOrThrowException(id);
        if (amount > trans.getAmount()) {
            throw APIException.badRequest("Refund amount is more than the receipt amount");
        }
        Transaction toSave = new Transaction();
        toSave.setType(TranxType.refund);
        toSave.setStatus("succeeded");
        toSave.setReceiptNo(UUID.randomUUID().toString());
        toSave.setParentTransaction(trans);
        toSave.setInvoice(trans.getInvoice());
        toSave.setCurrency(trans.getCurrency());
        toSave.setPayer(trans.getPayer());
        toSave.setCreditNote(trans.getCreditNote());
        toSave.setAmount(amount);
        toSave.setShiftNo("0000");

        Transaction trns = transactionRepository.save(toSave);
        return TransactionData.map(trns);

    }

    public Transaction findTransactionOrThrowException(Long id) {
        return findById(id)
                .orElseThrow(() -> APIException.notFound("Transaction with id {0} not found.", id));
    }

}
