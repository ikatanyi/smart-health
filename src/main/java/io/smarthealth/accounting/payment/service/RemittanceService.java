/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.service;

import io.smarthealth.accounting.accounts.data.FinancialActivity;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccount;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccountRepository;
import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.accounting.billing.data.PatientReceipt;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.cashier.domain.Shift;
import io.smarthealth.accounting.cashier.domain.ShiftRepository;
import io.smarthealth.accounting.payment.data.CreateRemittance;
import io.smarthealth.accounting.payment.data.PayChannel;
import io.smarthealth.accounting.payment.domain.Banking;
import io.smarthealth.accounting.payment.domain.Receipt;
import io.smarthealth.accounting.payment.domain.repository.ReceiptRepository;
import io.smarthealth.accounting.payment.domain.ReceiptTransaction;
import io.smarthealth.accounting.payment.domain.Remittance;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.payment.domain.PaymentDeposit;
import io.smarthealth.accounting.payment.domain.enumeration.PayerType;
import io.smarthealth.accounting.payment.domain.enumeration.RecordType;
import io.smarthealth.accounting.payment.domain.repository.RemittanceRepository;
import io.smarthealth.accounting.payment.domain.enumeration.TrnxType;
import io.smarthealth.accounting.payment.domain.repository.ReceivePaymenttRepository;
import io.smarthealth.accounting.payment.domain.specification.RemittanceSpecification;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.PayerRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.lang.SystemUtils;
import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
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
    private final BillingService billingService;
    private final ReceivePaymenttRepository receivePaymenttRepository;
    private final PatientRepository patientRepository;
//    private final AccountRepository accountRepository;

    public Receipt createRemittance(CreateRemittance data) {


        Receipt receipt = new Receipt();
        receipt.setPrepayment(Boolean.FALSE);
        receipt.setAmount(data.getAmount());
        receipt.setCurrency(data.getCurrency());
        receipt.setPayer(data.getPayerName());
        receipt.setPaymentMethod(data.getPaymentMethod());
        receipt.setReferenceNumber(data.getReferenceNumber());
        receipt.setRefundedAmount(BigDecimal.ZERO);
        receipt.setPaid(data.getAmount());
        receipt.setTenderedAmount(BigDecimal.ZERO);
        if (data.getShiftNo() != null) {
            Shift shift = shiftRepository.findByShiftNo(data.getShiftNo()).orElse(null);
            receipt.setShift(shift);
        }
        receipt.setTransactionDate(LocalDateTime.now());
        receipt.setDescription(data.getNotes());
        receipt.setReceivedFrom(data.getReceivedFrom());
        receipt.setPrepayment((data.getRecordType() == RecordType.Deposit));

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
        //
        if (data.getRecordType() == RecordType.Payment) {
            if (data.getPayerType() == PayerType.Insurance) {
                        Payer payer = payerRepository.findById(data.getPayerId())
                                    .orElseThrow(() -> APIException.notFound("Payer ID {0} Not Found", data.getPayerId()));
                Remittance remittance = new Remittance(payer, savedReceipt);
                repository.save(remittance);
            }
            if (data.getPayerType() == PayerType.Patient) {
                if (data.getVisitNumber() != null) {
                    PatientBill bill = billingService.createReceipt(new PatientReceipt(data.getVisitNumber(), savedReceipt));
                    //journal 
                }
            }
            journalService.save(toJournal(savedReceipt, data));

        }
        if (data.getRecordType() == RecordType.Deposit) {
            PaymentDeposit deposit = new PaymentDeposit();
            deposit.setAmount(savedReceipt.getAmount());
            deposit.setBalance(savedReceipt.getAmount());
            deposit.setCustomerType(data.getPayerType());
            deposit.setDescription(data.getNotes());
            deposit.setPaymentDate(data.getDate());
            deposit.setPaymentMethod(data.getPaymentMethod());
            deposit.setReceipt(savedReceipt);
            deposit.setReference(data.getReferenceNumber());
            deposit.setTransactionNo(savedReceipt.getTransactionNo());
            deposit.setType(data.getRecordType());

            if (data.getPayerType() == PayerType.Patient) {
                Patient patient = patientRepository.findByPatientNumber(data.getPayerNumber())
                        .orElseThrow(() -> APIException.notFound("Patient Number {} Not Found", data.getPayerNumber()));
                deposit.setCustomer(patient.getFullName());
                deposit.setCustomerId(patient.getId());
                deposit.setCustomerNumber(patient.getPatientNumber());
                if (data.getVisitNumber() != null) {
                    PatientBill bill = billingService.createReceipt(new PatientReceipt(data.getVisitNumber(), savedReceipt));
                    //journal 
                }
            }

            if (data.getPayerType() == PayerType.Insurance) {
                Payer pr = payerRepository.findById(data.getPayerId())
                        .orElseThrow(() -> APIException.notFound("Payer with ID  {} Not Found", data.getPayerId()));
                deposit.setCustomer(pr.getPayerName());
                deposit.setCustomerId(pr.getId());
                deposit.setCustomerNumber(pr.getPayerCode());
            }

            receivePaymenttRepository.save(deposit);
            journalService.save(toJournalDeposits(savedReceipt, data));
        }

        // journalService.save(toJournal(payer, receipt, data));
        // we figure out on the journaling processes
        return savedReceipt;
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

    private JournalEntry toJournal(Receipt receipt, CreateRemittance data) {

        List<JournalEntryItem> items = new ArrayList<>();

        String narration = "Payment of " + receipt.getAmount() + " from " + data.getPayerName() + " Receipt No. " + receipt.getReceiptNo();
        //determing the debit account - I think should be just receipt control
        if (data.getPayerType() == PayerType.Insurance) {
            Payer pr = payerRepository.findById(data.getPayerId())
                    .orElseThrow(() -> APIException.notFound("Payer with ID  {} Not Found", data.getPayerId()));
            items.add(new JournalEntryItem(pr.getDebitAccount(), narration, BigDecimal.ZERO, receipt.getAmount()));
        }
        if (data.getPayerType() == PayerType.Patient) {
            Optional<FinancialActivityAccount> patientControl = activityAccountRepository.findByFinancialActivity(FinancialActivity.Patient_Control);
            if (!patientControl.isPresent()) {
                throw APIException.badRequest("Patient Control Account is Not Mapped");
            }
            items.add(new JournalEntryItem(patientControl.get().getAccount(), narration, BigDecimal.ZERO, receipt.getAmount()));
        }
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

        String description = " Payment Received.  Receipt No." + receipt.getReceiptNo();

        JournalEntry toSave = new JournalEntry(receipt.getTransactionDate().toLocalDate(), description, items);
        toSave.setTransactionType(TransactionType.Payment);
        toSave.setTransactionNo(receipt.getTransactionNo());
        toSave.setStatus(JournalState.PENDING);

        return toSave;
    }

    private JournalEntry toJournalDeposits(Receipt payment, CreateRemittance data) {
        Optional<FinancialActivityAccount> creditAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.DeferredRevenue);

        if (!creditAccount.isPresent()) {
            throw APIException.badRequest("Deferred Revenue (Deposit) Account is Not Mapped for Transaction");
        }

        PayChannel channel = data.getPaymentChannel();

        List<JournalEntryItem> items = new ArrayList<>();
        String descType = "";
        String liabilityNarration = "Patient Deposit " + payment.getAmount() + " " + (data.getNotes() != null ? "(" + data.getNotes() + ")" : "");
        items.add(new JournalEntryItem(creditAccount.get().getAccount(), liabilityNarration, BigDecimal.ZERO, payment.getAmount()));
        //create the invoice payments
        descType = "Patient Payment deposit";

        //PAYMENT CHANNEL
        String narration = descType + "  for " + payment.getDescription() + " Reference No : " + payment.getReceiptNo();
        if (channel.getType() == PayChannel.Type.Bank) {
            BankAccount bank = bankingService.findBankAccountByNumber(channel.getAccountNumber())
                    .orElseThrow(() -> APIException.notFound("Bank Account Number {0} Not Found", channel.getAccountNumber()));
            Account debitAccount = bank.getLedgerAccount();
            //withdraw this amount from this bank
            bankingService.deposit(bank, payment, data.getAmount());
            items.add(new JournalEntryItem(debitAccount, narration, payment.getAmount(), BigDecimal.ZERO));
            //at this pointwithdraw the cash
        } else if (channel.getType() == PayChannel.Type.Cash) {
            Account debitAccount = null;
            if (channel.getAccountId() == 1) {
                Optional<FinancialActivityAccount> pettycashAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Petty_Cash);
                if (!pettycashAccount.isPresent()) {
                    throw APIException.badRequest("Petty Cash Account is Not Mapped");
                }
                debitAccount = pettycashAccount.get().getAccount();
            }
            if (channel.getAccountId() == 2) {
                Optional<FinancialActivityAccount> receiptAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Receipt_Control);
                if (!receiptAccount.isPresent()) {
                    throw APIException.badRequest("Undeposited Fund Account (Receipt Control) is Not Mapped");
                }
                debitAccount = receiptAccount.get().getAccount();
            }
            if (debitAccount == null) {
                return null;
            }
            items.add(new JournalEntryItem(debitAccount, narration, payment.getAmount(), BigDecimal.ZERO));
        }

        String description = descType + " Reference No " + payment.getReceiptNo();

        JournalEntry toSave = new JournalEntry(payment.getTransactionDate().toLocalDate(), description, items);
        toSave.setTransactionType(TransactionType.Receipting);
        toSave.setTransactionNo(payment.getTransactionNo());
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }

}
