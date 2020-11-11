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
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.cashier.domain.Shift;
import io.smarthealth.accounting.cashier.domain.ShiftRepository;
import io.smarthealth.accounting.payment.data.PayChannel;
import io.smarthealth.accounting.payment.data.CreateReceipt;
import io.smarthealth.accounting.payment.domain.PaymentDeposit;
import io.smarthealth.accounting.payment.domain.Receipt;
import io.smarthealth.accounting.payment.domain.enumeration.CustomerType;
import io.smarthealth.accounting.payment.domain.enumeration.ReceiveType;
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
import io.smarthealth.accounting.payment.domain.repository.ReceivePaymenttRepository;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePointsss;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.PayerRepository;
import io.smarthealth.stock.stores.domain.Store;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class ReceivePaymentService {

    private final SequenceNumberService sequenceNumberService;
    private final FinancialActivityAccountRepository activityAccountRepository;
    private final JournalService journalEntryService;
    private final ShiftRepository shiftRepository;
    private final ReceiptRepository repository;
    private final BankingService bankingService;
    private final ReceivePaymenttRepository receivePaymenttRepository;
    private final PatientRepository patientRepository;
    private final PayerRepository payerRepository;
    private final BillingService billingService;
    private final ServicePointService servicePointService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Receipt receivePayment(CreateReceipt data) {
        //get the visit number if any
        Receipt receipt = new Receipt();
        receipt.setPrepayment(Boolean.TRUE);
        receipt.setAmount(data.getAmount());
        receipt.setCurrency(data.getCurrency());
        receipt.setPayer(data.getCustomer());
        receipt.setPaid(data.getAmount());
        receipt.setPaymentMethod(data.getPaymentMethod());
//        receipt.setTenderedAmount(data.getTenderedAmount() != null ? data.getTenderedAmount() : BigDecimal.ZERO);
        receipt.setReferenceNumber(data.getReference());
        receipt.setRefundedAmount(BigDecimal.ZERO);
        if (data.getShiftNo() != null) {
            Shift shift = shiftRepository.findByShiftNo(data.getShiftNo()).orElse(null);
            receipt.setShift(shift);
        }
        receipt.setTransactionDate(data.getPaymentDate().atTime(LocalTime.now()));
        receipt.setDescription(data.getDescription());

        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String receiptNo = sequenceNumberService.next(1L, Sequences.Receipt.name());

        receipt.setTransactionNo(trdId);
        receipt.setReceiptNo(receiptNo);
        receipt.setPrepayment(Boolean.TRUE);

        Receipt savedReceipt = repository.save(receipt);

        //TODO payment deposit
        if (data.getType() == ReceiveType.Deposit) {
            PaymentDeposit deposit = new PaymentDeposit();
            deposit.setAmount(savedReceipt.getAmount());
            deposit.setBalance(savedReceipt.getAmount());
            deposit.setCustomerType(data.getCustomerType());
            deposit.setDescription(data.getDescription());
            deposit.setPaymentDate(data.getPaymentDate());
            deposit.setPaymentMethod(data.getPaymentMethod());
            deposit.setReceipt(savedReceipt);
            deposit.setReference(data.getReference());
            deposit.setTransactionNo(savedReceipt.getTransactionNo());
            deposit.setType(data.getType());

            if (data.getCustomerType() == CustomerType.Patient) {
                Patient patient = patientRepository.findByPatientNumber(data.getCustomerNumber())
                        .orElseThrow(() -> APIException.notFound("Patient Number {} Not Found", data.getCustomerNumber()));
                deposit.setCustomer(patient.getFullName());
                deposit.setCustomerId(patient.getId());
                deposit.setCustomerNumber(patient.getPatientNumber());
            }

            if (data.getCustomerType() == CustomerType.Insurance) {
                Payer payer = payerRepository.findById(data.getCustomerId())
                        .orElseThrow(() -> APIException.notFound("Payer with ID  {} Not Found", data.getCustomerId()));
                deposit.setCustomer(payer.getPayerName());
                deposit.setCustomerId(payer.getId());
                deposit.setCustomerNumber(payer.getPayerCode());
            }

            receivePaymenttRepository.save(deposit);

            journalEntryService.save(toJournalDeposits(savedReceipt, data));
        }
        if (data.getType() == ReceiveType.Payment) { 
            if (data.getCustomerType() == CustomerType.Patient) { 
                PatientBill bill = billingService.createReceipt(new PatientReceipt(data.getVisitNumber(), savedReceipt)); 
                journalEntryService.save(toJournalPayment(bill.getBillItems().get(0)));
            }
        }
        return savedReceipt;
    }

    public Optional<PaymentDeposit> getPrepayment(Long id) {
        return receivePaymenttRepository.findById(id);
    }

    public PaymentDeposit getPaymentOrThrow(Long id) {
        return getPrepayment(id)
                .orElseThrow(() -> APIException.notFound("Payment with Id {0} Not Found", id));
    }

    public Page<PaymentDeposit> getPayments(String customerNumber, String receiptNo, Boolean hasBalance, DateRange range, Pageable page) {
        Specification<PaymentDeposit> spec = PrepaymentSpecification.createSpecification(customerNumber, receiptNo, hasBalance, range);
        return receivePaymenttRepository.findAll(spec, page);
    }

    private JournalEntry toJournalDeposits(Receipt payment, CreateReceipt data) {
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

    private JournalEntry toJournalPayment(PatientBillItem bill) {

        Optional<FinancialActivityAccount> debitAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Patient_Control);
        if (!debitAccount.isPresent()) {
            throw APIException.badRequest("Patient Control Account is Not Mapped");
        }

        List<JournalEntryItem> items = new ArrayList<>();

        ServicePointsss srv = servicePointService.getServicePointByType(ServicePointType.Inpatient);

        String desc = "Patient Receipt Payment, Receipt No. " + bill.getPaymentReference();
        Account credit = srv.getIncomeAccount();
        BigDecimal amount = BigDecimal.valueOf(bill.getAmount()).negate();

        items.add(new JournalEntryItem(debitAccount.get().getAccount(), desc, amount, BigDecimal.ZERO));
        items.add(new JournalEntryItem(credit, desc, BigDecimal.ZERO, amount));

        JournalEntry toSave = new JournalEntry(bill.getBillingDate(), "Receipt Payment. Receipt No." + bill.getPaymentReference(), items);
        toSave.setTransactionNo(bill.getTransactionId());
        toSave.setTransactionType(TransactionType.Receipting);
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }
}
