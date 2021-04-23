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
import io.smarthealth.accounting.doctors.data.DoctorInvoiceStatus;
import io.smarthealth.accounting.doctors.domain.DoctorInvoice;
import io.smarthealth.accounting.doctors.domain.DoctorInvoiceRepository;
import io.smarthealth.accounting.payment.data.BillToPay;
import io.smarthealth.accounting.payment.data.PayChannel;
import io.smarthealth.accounting.payment.data.MakePayment;
import io.smarthealth.accounting.payment.data.MakePettyCashPayment;
import io.smarthealth.accounting.payment.data.PettyCashPaymentData;
import io.smarthealth.accounting.payment.data.PettyCashRequestItem;
import io.smarthealth.accounting.payment.data.SupplierPaymentData;
import io.smarthealth.accounting.payment.domain.DoctorsPayment;
import io.smarthealth.accounting.payment.domain.repository.DoctorsPaymentRepository;
import io.smarthealth.accounting.payment.domain.Payment;
import io.smarthealth.accounting.payment.domain.repository.PaymentRepository;
import io.smarthealth.accounting.payment.domain.PettyCashPayment;
import io.smarthealth.accounting.payment.domain.repository.PettyCashPaymentRepository;
import io.smarthealth.accounting.pettycash.data.enums.PettyCashStatus;
import io.smarthealth.accounting.pettycash.domain.PettyCashRequests;
import io.smarthealth.accounting.pettycash.service.PettyCashRequestsService;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.accounting.payment.domain.SupplierPayment;
import io.smarthealth.accounting.payment.domain.repository.SupplierPaymentRepository;
import io.smarthealth.accounting.payment.domain.enumeration.PayeeType;
import io.smarthealth.accounting.payment.domain.specification.PaymentSpecification;
import io.smarthealth.accounting.pettycash.domain.PettyCashRequestItems;
import io.smarthealth.accounting.pettycash.domain.repository.PettyCashApprovedItemsRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.lang.SystemUtils;
import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.stock.purchase.domain.PurchaseInvoice;
import io.smarthealth.stock.purchase.domain.PurchaseInvoiceRepository;
import io.smarthealth.supplier.domain.Supplier;
import io.smarthealth.supplier.domain.SupplierRepository;

import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * @author Kelsas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MakePaymentService {

    private final SequenceNumberService sequenceNumberService;
    private final FinancialActivityAccountRepository activityAccountRepository;
    private final BankingService bankingService;
    private final DoctorInvoiceRepository doctorInvoiceRepository;
    private final PurchaseInvoiceRepository purchaseInvoiceRepository;
    private final PaymentRepository repository;
    private final SupplierRepository supplierRepository;
    private final DoctorsPaymentRepository doctorsPaymentRepository;
    private final SupplierPaymentRepository supplierPaymentRepository;
    private final JournalService journalEntryService;
    private final PettyCashPaymentRepository pettyCashPaymentRepository;
    private final AccountRepository accountRepository;
    private final PettyCashApprovedItemsRepository pettyCashApprovedItemsRepository;
    private final PettyCashRequestsService pettyCashRequestsService;


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Payment makePayment(MakePayment data) {
        Payment payment = new Payment();
        payment.setPayeeId(data.getCreditorId());
        payment.setPayee(data.getCreditor());
        payment.setPayeeType(data.getCreditorType());
        payment.setAmount(data.getAmount());
        payment.setDescription(data.getDescription());
        payment.setPaymentDate(data.getDate());
        payment.setPaymentMethod(data.getPaymentMethod());
        payment.setReferenceNumber(data.getReferenceNumber());
//        payment.setSupplier(supplier);
        //update the invoices paid

        String transactionId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String voucherNo = sequenceNumberService.next(1L, Sequences.VoucherNumber.name());
        payment.setVoucherNo(voucherNo);
        payment.setTransactionNo(transactionId);

        Payment savedPayment = repository.save(payment);
        //update bills status and the once paid
        doBillPayment(payment, data);
        //Bank Charges 
        // journal the payment
        journalEntryService.save(toJournal(data.getCreditorType(), payment, data));
        return savedPayment;
    }

    public Optional<Payment> getPayment(Long id) {
        return repository.findById(id);
    }

    public Payment getPaymentOrThrow(Long id) {
        return getPayment(id)
                .orElseThrow(() -> APIException.notFound("Payment with Id {0} Not Found", id));
    }

    public Payment getPaymentByVoucherNo(String voucherNo) {
        return repository.findByVoucherNo(voucherNo)
                .orElseThrow(() -> APIException.notFound("Payment with Voucher Number {0} Not Found", voucherNo));
    }

    public Page<Payment> getPayments(PayeeType creditorType, Long creditorId, String creditor, String transactionNo, DateRange range, Pageable page) {
        Specification<Payment> spec = PaymentSpecification.createSpecification(creditorType, creditorId, creditor, transactionNo, range);
        return repository.findAll(spec, page);
    }

    private void doBillPayment(Payment payment, MakePayment data) {
        switch (data.getCreditorType()) {
            case Doctor: {
                List<DoctorsPayment> list = data.getInvoices()
                        .stream().map(x -> doctorsPayment(payment, x))
                        .filter(x -> x != null)
                        .collect(Collectors.toList());
                doctorsPaymentRepository.saveAll(list);
            }
            break;
            case Supplier: {
                List<SupplierPayment> list = data.getInvoices()
                        .stream().map(x -> supplierPayment(payment, x))
                        .filter(x -> x != null)
                        .collect(Collectors.toList());
                supplierPaymentRepository.saveAll(list);
            }
            break;
            default:
        }
    }

    private SupplierPayment supplierPayment(Payment pay, BillToPay bill) {
        Optional<PurchaseInvoice> invoice = purchaseInvoiceRepository.findByInvoiceForSupplier(bill.getInvoiceNo(), pay.getPayeeId());
        if (invoice.isPresent()) {
            PurchaseInvoice inv = invoice.get();
            BigDecimal grosspaid = bill.getAmountPaid().add(bill.getTaxAmount());
            BigDecimal newBal = inv.getInvoiceBalance().subtract(grosspaid);
            boolean paid = newBal.doubleValue() <= 0;
            inv.setInvoiceBalance(newBal);
            inv.setPaid(paid);
            inv.setTax(bill.getTaxAmount());
            PurchaseInvoice saved = purchaseInvoiceRepository.save(inv);
            return new SupplierPayment(pay, saved, bill.getAmountPaid(), bill.getTaxAmount());
        }
        return null;
    }

    private DoctorsPayment doctorsPayment(Payment pay, BillToPay bill) {
        Optional<DoctorInvoice> invoice = doctorInvoiceRepository.findByInvoiceForDoctor(bill.getInvoiceNo(), pay.getPayeeId());
        if (invoice.isPresent()) {
            DoctorInvoice inv = invoice.get();
            BigDecimal grosspaid = bill.getAmountPaid().add(bill.getTaxAmount());
            BigDecimal newBal = inv.getBalance().subtract(grosspaid);
            boolean paid = newBal.doubleValue() <= 0;
            inv.setBalance(newBal);
            inv.setPaid(paid);
            if(paid){
                inv.setInvoiceStatus(DoctorInvoiceStatus.Paid);
            }
            DoctorInvoice saved = doctorInvoiceRepository.save(inv);
            return new DoctorsPayment(pay, saved, bill.getAmountPaid(), bill.getTaxAmount());
        }
        return null;
    }

    private JournalEntry toJournal(PayeeType type, Payment payment, MakePayment data) {

        PayChannel channel = data.getPaymentChannel();

        List<JournalEntryItem> items = new ArrayList<>();
        String descType = "";
        Account creditTax = null;
        switch (type) {
            case Doctor: {
                Optional<FinancialActivityAccount> debitAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Doctors_Fee);
                if (!debitAccount.isPresent()) {
                    throw APIException.badRequest("Doctors Ledger Account is Not Mapped");
                }
                Account acc = debitAccount.get().getAccount();
                String narration = "Doctor's Payment Voucher no. " + payment.getVoucherNo();
                items.add(new JournalEntryItem(acc, narration, payment.getAmount(), BigDecimal.ZERO));
                //create the invoice payments
                descType = "Doctor's Payment";
                creditTax = acc;
            }
            break;
            case Supplier: {
                Supplier supplier = getSupplier(payment.getPayeeId());
                Account acc = supplier.getCreditAccount();
                String narration = "Supplier's Payment Voucher no. " + payment.getVoucherNo();
                items.add(new JournalEntryItem(acc, narration, payment.getAmount(), BigDecimal.ZERO));
                descType = "Supplier's Payment";
                creditTax = acc;
                purchaseInvoiceRepository.save(createPaymentEntry(payment));
            }
            break;
            case Others: {

                Account acc = accountRepository.findByIdentifier(data.getExpenseAccount()).get();
                String narration = "Miscellaneous Expense's Payment Voucher no. " + payment.getVoucherNo();
                items.add(new JournalEntryItem(acc, narration, payment.getAmount(), BigDecimal.ZERO));
                descType = "Other's Payment";
                creditTax = acc;
            }
            default:
        }
        //PAYMENT CHANNEL
        String narration = "Payment for voucher no. " + payment.getVoucherNo() + " amount : " + SystemUtils.formatCurrency(payment.getAmount());
        if (channel.getType() == PayChannel.Type.Bank) {
            BankAccount bank = bankingService.findBankAccountByNumber(channel.getAccountNumber())
                    .orElseThrow(() -> APIException.notFound("Bank Account Number {0} Not Found", channel.getAccountNumber()));
            Account creditAccount = bank.getLedgerAccount();
            //withdraw this amount from this bank
            bankingService.withdraw(bank, payment);
            if (data.getBankCharge() != null && data.getBankCharge() != BigDecimal.ZERO) {
                Optional<FinancialActivityAccount> debitAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Bank_Charge);
                if (debitAccount.isPresent()) {
                    Account debitAcc = debitAccount.get().getAccount();
                    bankingService.bankingCharges(bank, data.getBankCharge(), payment.getTransactionNo());
                    String desc = "Bank Charge of " + SystemUtils.formatCurrency(data.getBankCharge());
                    items.add(new JournalEntryItem(debitAcc, desc, data.getBankCharge(), BigDecimal.ZERO));
                    items.add(new JournalEntryItem(creditAccount, desc, BigDecimal.ZERO, data.getBankCharge()));
                }
            }

            items.add(new JournalEntryItem(creditAccount, narration, BigDecimal.ZERO, payment.getAmount()));

            //at this pointwithdraw the cash
        } else if (channel.getType() == PayChannel.Type.Cash) {
            Account creditAccount = null;
            if (channel.getAccountId() == 1) {
                Optional<FinancialActivityAccount> pettycashAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Petty_Cash);
                if (!pettycashAccount.isPresent()) {
                    throw APIException.badRequest("Petty Cash Account is Not Mapped");
                }
                creditAccount = pettycashAccount.get().getAccount();
            }
            if (channel.getAccountId() == 2) {
                Optional<FinancialActivityAccount> receiptAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Receipt_Control);
                if (!receiptAccount.isPresent()) {
                    throw APIException.badRequest("Undeposited Fund Account (Receipt Control) is Not Mapped");
                }
                creditAccount = receiptAccount.get().getAccount();
            }
            if (creditAccount == null) {
                return null;
            }
            items.add(new JournalEntryItem(creditAccount, narration, BigDecimal.ZERO, payment.getAmount()));
        }
        //TAXES
        if (data.getTaxAccountNumber() != null) {
            Optional<Account> taxAccount = accountRepository.findByIdentifier(data.getTaxAccountNumber());
            if (taxAccount.isPresent()) {
                //then we go tax
                Account debitTax = taxAccount.get();
                final Account toCreditTax = creditTax;
                BigDecimal amount = data.getInvoices()
                        .stream()
                        .filter(x -> x.getTaxAmount() != null && x.getTaxAmount() != BigDecimal.ZERO)
                        .map(x -> x.getTaxAmount())
                        .reduce(BigDecimal.ZERO, (x, y) -> x.add(y));
//                        .forEach(inv -> {
                String desc = "Withheld Tax " + SystemUtils.formatCurrency(amount) + " for the payment voucher no. " + payment.getVoucherNo();
                items.add(new JournalEntryItem(debitTax, desc, amount, BigDecimal.ZERO));
                items.add(new JournalEntryItem(toCreditTax, desc, BigDecimal.ZERO, amount));
//                        });
            }
        }

        String description = descType + " - Voucher Number. " + payment.getVoucherNo();

        JournalEntry toSave = new JournalEntry(payment.getPaymentDate(), description, items);
        toSave.setTransactionType(TransactionType.Payment);
        toSave.setTransactionNo(payment.getTransactionNo());
        toSave.setStatus(JournalState.PENDING);

        return toSave;
    }

    public Supplier getSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Supplier with id {0} not found.", id));
        return supplier;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Payment makePayment(MakePettyCashPayment data) {
        Payment payment = new Payment();
        payment.setPayeeId(data.getPayeeId());
        payment.setPayee(data.getPayee());
        payment.setPayeeType(PayeeType.PettyCash);
        payment.setAmount(data.getApprovedAmount());
        payment.setDescription(data.getDescription());
        payment.setPaymentDate(data.getDate());
        payment.setPaymentMethod("Cash");
        payment.setReferenceNumber(data.getReferenceNumber());
//        payment.setSupplier(supplier);
        //update the invoices paid

        String transactionId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String voucherNo = sequenceNumberService.next(1L, Sequences.VoucherNumber.name());
        payment.setVoucherNo(voucherNo);
        payment.setTransactionNo(transactionId);

        Payment savedPayment = repository.save(payment);
        //update bills status and the once paid
        PettyCashRequests re = pettyCashRequestsService.fetchCashRequestByRequestNo(data.getReferenceNumber());
        re.setPaid(Boolean.TRUE);
        re.setStatus(PettyCashStatus.Paid);
        pettyCashRequestsService.createCashRequests(re);

        List<PettyCashPayment> pettycashpayments = data.getRequests()
                .stream()
                .map(req -> {
                    PettyCashPayment pay = new PettyCashPayment();
                    pay.setCredit(req.getAmount());
                    pay.setDebit(BigDecimal.ZERO);
                    pay.setDescription(req.getDescription());
                    pay.setPayee(data.getPayee());
                    pay.setPayment(savedPayment);
                    pay.setPaymentDate(data.getDate());
//                   pay.setPettyCashRequest(pettyCashRequest);
                    pay.setTransactionNo(savedPayment.getTransactionNo());
                    pay.setVoucherNo(savedPayment.getVoucherNo());
                    if (req.getRequestId() != null) {
                        pettyCashApprovedItemsRepository.updateItemPaid(req.getRequestId());
                    }
                    return pay;
                })
                .collect(Collectors.toList());
        pettyCashPaymentRepository.saveAll(pettycashpayments);
        // journal the payment
        journalEntryService.save(toJournalPettyCash(payment, data.getRequests()));

        return savedPayment;
    }

    public List<PettyCashPaymentData> getPettyCashPayment(Long id) {
        List<PettyCashPaymentData> pettyCashPayment = null;
        Optional<Payment> payment = repository.findById(id);
        if (payment.isPresent()) {
            pettyCashPayment = pettyCashPaymentRepository.findByPayment(payment.get())
                    .stream()
                    .map((petty) -> petty.toData())
                    .collect(Collectors.toList());
        }
        return pettyCashPayment;
    }

    public List<SupplierPaymentData> getSupplierPayment(Long id, PayeeType type) {
        List<SupplierPaymentData> supplierPayment = null;
        Optional<Payment> payment = repository.findById(id);
        if (payment.isPresent()) {
            if (type == PayeeType.Supplier) {
                supplierPayment = supplierPaymentRepository.findByPayment(payment.get())
                        .stream()
                        .map((supplier) -> supplier.toData())
                        .collect(Collectors.toList());
            } else {
                supplierPayment = doctorsPaymentRepository.findByPayment(payment.get())
                        .stream()
                        .map((supplier) -> supplier.toData())
                        .collect(Collectors.toList());
            }
        }
        return supplierPayment;
    }

    private void taxJournaling(MakePayment data, List<JournalEntryItem> items) {

    }

    private JournalEntry toJournalPettyCash(Payment payment, List<PettyCashRequestItem> requests) {

        List<JournalEntryItem> items = new ArrayList<>();
        requests.stream()
                .forEach(request -> {
                            //credit the account we paying from and not his
                            Account debitAccount = findAccountByNumber(request.getLedgerAccountNumber());

                            String narration = SystemUtils.formatCurrency(payment.getAmount()) + " for petty cash payment, voucher no. " + payment.getVoucherNo();
                            items.add(new JournalEntryItem(debitAccount, narration, request.getAmount(), BigDecimal.ZERO));
                        }
                );

        Optional<FinancialActivityAccount> pettycashAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Petty_Cash);
        if (!pettycashAccount.isPresent()) {
            throw APIException.badRequest("Petty Cash Account is Not Mapped");
        }
        Account creditAccount = pettycashAccount.get().getAccount();
        String narration = SystemUtils.formatCurrency(payment.getAmount()) + " for petty cash payment, voucher no. " + payment.getVoucherNo();
        items.add(new JournalEntryItem(creditAccount, narration, BigDecimal.ZERO, payment.getAmount()));

        String description = "Petty Cash Payment - Voucher Number. " + payment.getVoucherNo() + " amount : " + SystemUtils.formatCurrency(payment.getAmount());
        JournalEntry toSave = new JournalEntry(payment.getPaymentDate(), description, items);
        toSave.setTransactionType(TransactionType.Petty_Cash);
        toSave.setTransactionNo(payment.getTransactionNo());
        toSave.setStatus(JournalState.PENDING);

        return toSave;
    }

    public Account findAccountByNumber(String identifier) {
        return accountRepository.findByIdentifier(identifier)
                .orElseThrow(() -> APIException.notFound("Account with Account Number {0} not found", identifier));
    }

    private PurchaseInvoice createPaymentEntry(Payment payment){
        Supplier supplier = getSupplier(payment.getPayeeId());
        PurchaseInvoice inv = new PurchaseInvoice();
        inv.setSupplier(supplier);
        inv.setPaid(true);
        inv.setInvoiceBalance(BigDecimal.ZERO);
        inv.setTax(BigDecimal.ZERO);
        inv.setStatus(PurchaseInvoiceStatus.Paid);
        inv.setDiscount(BigDecimal.ZERO);
        inv.setInvoiceNumber(payment.getReferenceNumber());
        inv.setApprovalDate(payment.getPaymentDate());
        inv.setApproved(true);
        inv.setInvoiceDate(payment.getPaymentDate());
        inv.setInvoiceBalance(BigDecimal.ZERO);
        inv.setInvoiceAmount(payment.getAmount().negate());
        inv.setIsReturn(false);
        inv.setNetAmount(payment.getAmount().negate());
        inv.setTransactionDate(payment.getPaymentDate());
        inv.setTransactionNumber(payment.getTransactionNo());
        inv.setType(PurchaseInvoice.Type.Payment);

        return  inv;
    }
}
