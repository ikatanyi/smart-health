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
import io.smarthealth.accounting.cashier.domain.Shift;
import io.smarthealth.accounting.cashier.domain.ShiftRepository;
import io.smarthealth.accounting.payment.data.PayChannel;
import io.smarthealth.accounting.payment.data.CreatePrepayment;
import io.smarthealth.accounting.payment.domain.Prepayment;
import io.smarthealth.accounting.payment.domain.Receipt;
import io.smarthealth.accounting.payment.domain.repository.PrepaymentRepository;
import io.smarthealth.accounting.payment.domain.repository.ReceiptRepository;
import io.smarthealth.accounting.payment.domain.specification.PrepaymentSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.lang.SystemUtils;
import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class PrepaymentService {

    private final SequenceNumberService sequenceNumberService;
    private final FinancialActivityAccountRepository activityAccountRepository;
    private final JournalService journalEntryService;
    private final ShiftRepository shiftRepository;
    private final ReceiptRepository repository;
    private final BankingService bankingService;
    private final PrepaymentRepository prepaymentRepository;
    private final PatientRepository patientRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Prepayment prepayments(CreatePrepayment data) {
        Patient patient = patientRepository.findByPatientNumber(data.getPatientNumber())
                .orElseThrow(() -> APIException.notFound("Patient Number {} Not Found", data.getPatientNumber()));

        //get the visit number if any
        Receipt receipt = new Receipt();
        receipt.setPrepayment(Boolean.TRUE);
        receipt.setAmount(data.getAmount());
        receipt.setCurrency(data.getCurrency());
        receipt.setPayer(patient.getFullName());

        receipt.setPaid(data.getAmount());
        receipt.setPaymentMethod(data.getPaymentMethod());
//        receipt.setTenderedAmount(data.getTenderedAmount() != null ? data.getTenderedAmount() : BigDecimal.ZERO);
        receipt.setReferenceNumber(data.getReferenceNo());
        receipt.setRefundedAmount(BigDecimal.ZERO);
        if (data.getShiftNo() != null) {
            Shift shift = shiftRepository.findByShiftNo(data.getShiftNo()).orElse(null);
            receipt.setShift(shift);
        }
        receipt.setTransactionDate(data.getPaymentDate().atTime(LocalTime.now()));
        receipt.setDescription(data.getNarration());

        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String receiptNo = sequenceNumberService.next(1L, Sequences.Receipt.name());

        receipt.setTransactionNo(trdId);
        receipt.setReceiptNo(receiptNo);

        Receipt savedReceipt = repository.save(receipt);

        Prepayment prepayment = new Prepayment();
        prepayment.setAmount(data.getAmount());
        prepayment.setBalance(data.getAmount());
        prepayment.setCurrency(data.getCurrency());
        prepayment.setMemo(data.getNarration());
        prepayment.setPatient(patient);
        prepayment.setPaymentDate(data.getPaymentDate());
        prepayment.setPaymentMethod(data.getPaymentMethod());
        prepayment.setReceipt(savedReceipt);

        Prepayment savedPrepayment = prepaymentRepository.save(prepayment);
        //post the journals
        journalEntryService.save(toJournalPrepayment(savedReceipt, data));

        return savedPrepayment;
    }

    public Optional<Prepayment> getPrepayment(Long id) {
        return prepaymentRepository.findById(id);
    }

    public Prepayment getPaymentOrThrow(Long id) {
        return getPrepayment(id)
                .orElseThrow(() -> APIException.notFound("Payment with Id {0} Not Found", id));
    } 
    public Page<Prepayment> getPayments(String patientNumber, String receiptNo, Boolean hasBalance, DateRange range, Pageable page) {
        Specification<Prepayment> spec = PrepaymentSpecification.createSpecification(patientNumber, receiptNo,hasBalance, range);
        return prepaymentRepository.findAll(spec, page);
    }

    private JournalEntry toJournalPrepayment(Receipt payment, CreatePrepayment data) {
        Optional<FinancialActivityAccount> creditAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.DeferredRevenue);

        if (!creditAccount.isPresent()) {
            throw APIException.badRequest("Deferred Revenue (Deposit) Account is Not Mapped for Transaction");
        }

        PayChannel channel = data.getDepositedTo();

        List<JournalEntryItem> items = new ArrayList<>();
        String descType = "";
        String liabilityNarration = SystemUtils.formatCurrency(payment.getAmount()) + " patient deposit for " + payment.getDescription();
        items.add(new JournalEntryItem(creditAccount.get().getAccount(), liabilityNarration, BigDecimal.ZERO, payment.getAmount()));
        //create the invoice payments
        descType = "Patient Prepayment/deposit";

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
