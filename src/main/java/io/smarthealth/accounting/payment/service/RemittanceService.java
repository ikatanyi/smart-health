/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.service;

import io.smarthealth.accounting.accounts.data.FinancialActivity;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.AccountRepository;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccount;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccountRepository;
import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.accounting.cashier.domain.Shift;
import io.smarthealth.accounting.cashier.domain.ShiftRepository;
import io.smarthealth.accounting.payment.data.CreateRemittance;
import io.smarthealth.accounting.payment.data.PayChannel;
import io.smarthealth.accounting.payment.domain.Banking;
import io.smarthealth.accounting.payment.domain.Receipt;
import io.smarthealth.accounting.payment.domain.repository.ReceiptRepository;
import io.smarthealth.accounting.payment.domain.ReceiptTransaction;
import io.smarthealth.accounting.payment.domain.Remittance;
import io.smarthealth.accounting.payment.domain.repository.RemittanceRepository;
import io.smarthealth.accounting.payment.domain.enumeration.TrnxType;
import io.smarthealth.accounting.payment.domain.specification.RemittanceSpecification;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.PayerRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.lang.SystemUtils;
import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class RemittanceService {

    private final RemittanceRepository repository;
    private final PayerRepository payerRepository;
    private final ShiftRepository shiftRepository;
    private final ReceiptRepository receiptRepository;
    private final SequenceNumberService sequenceNumberService;
    private final BankingService bankingService;
    private final JournalService journalService;
    private final FinancialActivityAccountRepository activityAccountRepository;
//    private final AccountRepository accountRepository;

    public Remittance createRemittance(CreateRemittance data) {
        Payer payer = payerRepository.findById(data.getPayerId())
                .orElseThrow(() -> APIException.notFound("Payer ID {0} Not Found", data.getPayerId()));

        Receipt receipt = new Receipt();
        receipt.setPrepayment(Boolean.FALSE);
        receipt.setAmount(data.getAmount());
        receipt.setCurrency(data.getCurrency());
        receipt.setPayer(payer.getPayerName());
        receipt.setPaymentMethod(data.getPaymentMethod());
        receipt.setReferenceNumber(data.getReferenceNumber());
        receipt.setRefundedAmount(BigDecimal.ZERO);
        if (data.getShiftNo() != null) {
            Shift shift = shiftRepository.findByShiftNo(data.getShiftNo()).orElse(null);
            receipt.setShift(shift);
        }
        receipt.setTransactionDate(LocalDateTime.now());
        receipt.setDescription("Insurance Payment");

        ReceiptTransaction trans = new ReceiptTransaction();
        trans.setCurrency(data.getCurrency());
        trans.setDatetime(LocalDateTime.now());
        trans.setAmount(data.getAmount());
        trans.setMethod(data.getPaymentMethod());
        trans.setReference(data.getReferenceNumber());
        trans.setType(TrnxType.Payment);

        receipt.addTransaction(trans);

        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String receiptNo = sequenceNumberService.next(1L, Sequences.Receipt.name());

        receipt.setTransactionNo(trdId);
        receipt.setReceiptNo(receiptNo);

        Receipt savedReceipt = receiptRepository.save(receipt); //save the payment
        Remittance remittance = new Remittance(payer, savedReceipt);

        repository.save(remittance);

        journalService.save(toJournal(payer, receipt, data));
        return remittance;
    }

    public Remittance save(Remittance remittance) {
        return repository.save(remittance);
    }

    public Optional<Remittance> getRemittance(Long id) {
        return repository.findById(id);
    }

    public Remittance getRemittanceOrThrow(Long id) {
        return getRemittance(id)
                .orElseThrow(() -> APIException.notFound("Remittance with id {0} Not Found", id));
    }

    public Page<Remittance> getRemittances(Long payerId, String receipt, String remittanceNo, Boolean hasBalance, DateRange range, Pageable page) {
        Specification<Remittance> spec = RemittanceSpecification.createSpecification(payerId, receipt, remittanceNo, hasBalance, range);
        return repository.findAll(spec, page);
    }
 
    private JournalEntry toJournal(Payer payer, Receipt receipt, CreateRemittance data) {

        List<JournalEntryItem> items = new ArrayList<>();

        String narration = "Remittance of " + SystemUtils.formatCurrency(receipt.getAmount()) + " from " + payer.getPayerName() + " Receipt No. " + receipt.getReceiptNo();
        items.add(new JournalEntryItem(payer.getDebitAccount(), narration, BigDecimal.ZERO, receipt.getAmount()));

        //PAYMENT CHANNEL
        narration = "Remittance No. " + receipt.getReceiptNo() + " amount : " + SystemUtils.formatCurrency(receipt.getAmount());

        if (data.getPaymentChannel().getType() == PayChannel.Type.Bank) {
            BankAccount bank = bankingService.findBankAccountByNumber(data.getPaymentChannel().getAccountNumber())
                    .orElseThrow(() -> APIException.notFound("Bank Account Number {0} Not Found", data.getPaymentChannel().getAccountNumber()));
            Account debitAccount = bank.getLedgerAccount();
            //withdraw this amount from this bank
            Banking banking = Banking.deposit(bank, receipt, receipt.getAmount());

            bankingService.save(banking);

//            if (data.getBankCharge() != null && data.getBankCharge() != BigDecimal.ZERO) {
//                Optional<FinancialActivityAccount> debitAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Bank_Charge);
//                if (debitAccount.isPresent()) {
//                    Account debitAcc = debitAccount.get().getAccount();
//                    bankingService.bankingCharges(bank, data.getBankCharge(), receipt.getReferenceNumber());
//                    String desc = "Bank Charge of " + SystemUtils.formatCurrency(data.getBankCharge());
//                    items.add(new JournalEntryItem(debitAcc, desc, data.getBankCharge(), BigDecimal.ZERO));
//                    items.add(new JournalEntryItem(debitAccount, desc, BigDecimal.ZERO, data.getBankCharge()));
//                }
//            }
            items.add(new JournalEntryItem(debitAccount, narration, receipt.getAmount(), BigDecimal.ZERO));

            //at this pointwithdraw the cash
        } else if (data.getPaymentChannel().getType() == PayChannel.Type.Cash) {
            Account debitAccount = null;
            if (data.getPaymentChannel().getAccountId() == 1) {
                Optional<FinancialActivityAccount> pettycashAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Petty_Cash);
                if (!pettycashAccount.isPresent()) {
                    throw APIException.badRequest("Petty Cash Account is Not Mapped");
                }
                debitAccount = pettycashAccount.get().getAccount();
            }
            if (data.getPaymentChannel().getAccountId() == 2) {
                Optional<FinancialActivityAccount> receiptAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Receipt_Control);
                if (!receiptAccount.isPresent()) {
                    throw APIException.badRequest("Undeposited Fund Account (Receipt Control) is Not Mapped");
                }
                debitAccount = receiptAccount.get().getAccount();
            }
            if (debitAccount == null) {
                return null;
            }
            items.add(new JournalEntryItem(debitAccount, narration, receipt.getAmount(), BigDecimal.ZERO));
        }
        //TAXES
//        if (data.getTaxAccountNumber() != null) {
//            Optional<Account> taxAccount = accountRepository.findByIdentifier(data.getTaxAccountNumber());
//            if (taxAccount.isPresent()) {
//                //then we go tax
//                Account debitTax = taxAccount.get();
//                final Account toCreditTax = creditTax;
//                BigDecimal amount =  receipt.getAmount();
//                String desc = "Sales Tax " + SystemUtils.formatCurrency(amount) + " for the Remittance no. " + receipt.getReferenceNumber();
//                items.add(new JournalEntryItem(debitTax, desc, amount, BigDecimal.ZERO));
//                items.add(new JournalEntryItem(toCreditTax, desc, BigDecimal.ZERO, amount));
////                        });
//            }
//        }

        String description = " Remittance Received.  Remittance No." + receipt.getReferenceNumber();

        JournalEntry toSave = new JournalEntry(receipt.getTransactionDate().toLocalDate(), description, items);
        toSave.setTransactionType(TransactionType.Payment);
        toSave.setTransactionNo(receipt.getTransactionNo());
        toSave.setStatus(JournalState.PENDING);

        return toSave;
    }

}
