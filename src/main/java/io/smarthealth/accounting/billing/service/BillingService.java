package io.smarthealth.accounting.billing.service;

import com.google.common.collect.Sets;
import io.smarthealth.accounting.acc.data.v1.Creditor;
import io.smarthealth.accounting.acc.data.v1.Debtor;
import io.smarthealth.accounting.acc.data.v1.JournalEntry;
import io.smarthealth.accounting.acc.service.JournalEntryService;
import io.smarthealth.accounting.acc.service.JournalEventSender;
import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.accounting.billing.data.BillItemData;
import io.smarthealth.accounting.billing.domain.Bill;
import io.smarthealth.accounting.billing.domain.BillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.domain.specification.BillingSpecification;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.SecurityUtils;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.infrastructure.sequence.service.SequenceService;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.accounting.billing.domain.BillRepository;
import io.smarthealth.accounting.billing.domain.BllItemRepository;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.infrastructure.utility.UuidGenerator;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BillingService {

    private final BillRepository patientBillRepository;
    private final BllItemRepository billItemRepository;
    private final VisitService visitService;
    private final ItemService itemService;

    private final SequenceService sequenceService;
    private final JournalEntryService journalService;
    private final ServicePointService servicePointService;

    public Bill createBill(BillData data) {
        //check the validity of the patient visit
        Visit visit = visitService.findVisitEntityOrThrow(data.getVisitNumber());
        String billNumber =RandomStringUtils.randomNumeric(6); //sequenceService.nextNumber(SequenceType.BillNumber);
        String trdId=UuidGenerator.newUuid();

        Bill patientbill = new Bill();
        patientbill.setVisit(visit);
        patientbill.setPatient(visit.getPatient());
        patientbill.setAmount(data.getAmount());
        patientbill.setDiscount(data.getDiscount());
        patientbill.setBalance(data.getAmount());
        patientbill.setBillNumber(billNumber);
        patientbill.setBillingDate(data.getBillingDate());
        patientbill.setJournalNumber(data.getJournalNumber());
        patientbill.setPaymentMode(data.getPaymentMode());
        patientbill.setTransactionId(trdId);
        patientbill.setStatus(BillStatus.Draft);

        List<BillItem> lineItems = data.getBillItems()
                .stream()
                .map(lineData -> {
                    BillItem billItem = new BillItem();

                    billItem.setBillingDate(lineData.getBillingDate());
                    billItem.setTransactionId(trdId);

                    if (lineData.getItemId() != null) {
                        Item item = itemService.findItemEntityOrThrow(lineData.getItemId());
                        billItem.setItem(item);
                    }

                    billItem.setPrice(lineData.getPrice());
                    billItem.setQuantity(lineData.getQuantity());
                    billItem.setAmount(lineData.getAmount());
                    billItem.setDiscount(lineData.getDiscount());
                    billItem.setBalance(lineData.getAmount());
                    billItem.setServicePoint(lineData.getServicePoint());
                    billItem.setStatus(BillStatus.Draft);

                    return billItem;
                })
                .collect(Collectors.toList());
        patientbill.addBillItems(lineItems);

        Bill savedBill = patientBillRepository.save(patientbill);

        journalService.createJournalEntry(toJournal(savedBill));

        //trigger stock balance if items is an inventory
//         journalSender.postJournal(toJournal(savedBill)); 
        return savedBill;
    }
    
    public Bill save(Bill bill){
        return patientBillRepository.save(bill);
    }

    public Optional<Bill> findByBillNumber(final String billNumber) {
        return patientBillRepository.findByBillNumber(billNumber);
    }

    public Bill findOneWithNoFoundDetection(Long id) {
        return patientBillRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Bill with Id {0} not found", id));
    }

    public BillItem findBillItemById(Long id) {
        return billItemRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Bill Item with Id {0} not found", id));
    }

    public BillItem updateBillItem(BillItem item) {
        return billItemRepository.save(item);
    }

    public String addPatientBillItems(Long id, List<BillItemData> billItems) {
        Bill patientbill = findOneWithNoFoundDetection(id);
        List<BillItem> lineItems = billItems
                .stream()
                .map(lineData -> {
                    BillItem billLine = new BillItem();

                    billLine.setBillingDate(lineData.getBillingDate());
                    billLine.setTransactionId(lineData.getTransactionId());

                    if (lineData.getItemId() != null) {
                        Item item = itemService.findItemEntityOrThrow(lineData.getItemId());
                        billLine.setItem(item);

                    }

                    billLine.setPrice(lineData.getPrice());
                    billLine.setQuantity(lineData.getQuantity());
                    billLine.setAmount(lineData.getAmount());
                    billLine.setDiscount(lineData.getDiscount());
                    billLine.setBalance(lineData.getAmount());
                    billLine.setServicePoint(lineData.getServicePoint());
                    billLine.setStatus(BillStatus.Draft);

                    return billLine;
                })
                .collect(Collectors.toList());
        patientbill.addBillItems(lineItems);

        return patientbill.getBillNumber();
    }

    public Page<Bill> findAllBills(String refNo, String visitNo, String patientNo, String paymentMode, String billNo, String status, Pageable page) {
        BillStatus state = BillStatus.valueOf(status);
        Specification<Bill> spec = BillingSpecification.createSpecification(refNo, visitNo, patientNo, paymentMode, billNo, state);

        return patientBillRepository.findAll(spec, page);

    }

//    private void createJournal(PatientBill bill) {
//        FinancialActivityAccount activity = financialActivityAccountService.getByTransactionType(FinancialActivity.Patient_Invoice_Control)
//                .orElseThrow(() -> APIException.badRequest("Patient Control Account for billing is not mapped"));
//
//        AccountEntity patientControlAccount = activity.getAccount();
//
//        JournalEntryEntity journal = new JournalEntryEntity();
//        journal.setTransactionType(TransactionType.Billing);
//        journal.setDocumentDate(bill.getBillingDate());
//        journal.setManualEntry(false);
//        journal.setReferenceNumber(bill.getReferenceNumber());
//        journal.setState(JournalState.APPROVED);
//        journal.setTransactionDate(bill.getBillingDate());
//        journal.setActivity(bill.getPatient().getPatientNumber());
//
//        bill.getBillLines()
//                .forEach(billItem -> {
//                    Account revenueAccount = pointsService.getServicePoint(billItem.getServicePointId()).getIncomeAccount();
//
//                    journal.addJournalEntry(new JournalEntry(patientControlAccount, 0D, billItem.getAmount(), billItem.getBillingDate(), String.format("Patient Billing %s bill number %s", bill.getPatient().getPatientNumber(), bill.getBillNumber())));
//                    journal.addJournalEntry(new JournalEntry(revenueAccount, billItem.getAmount(), 0D, billItem.getBillingDate(), String.format("Patient Billing %s bill number %s", bill.getPatient().getPatientNumber(), bill.getBillNumber())));
//
//                    if (billItem.getItem().isInventoryItem()) {
//                        Account expenseAccount = pointsService.getServicePoint(billItem.getServicePointId()).getIncomeAccount();
//                        journal.addJournalEntry(new JournalEntry(expenseAccount, 0D, billItem.getItem().getCostRate(), billItem.getBillingDate(), "Stocks Inventory"));
//                        journal.addJournalEntry(new JournalEntry(patientControlAccount, billItem.getItem().getCostRate(), 0D, billItem.getBillingDate(), "Stocks Inventory"));
//                    }
//                });
//
//        journalService.createJournalEntry(journal.toData());
//        //do a stock movement for the inventory at this point
//
//    }
    private JournalEntry toJournal(Bill bill) {

        final String roundedAmount = BigDecimal.valueOf(6500D).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString();

        final JournalEntry je = new JournalEntry();
        je.setTransactionIdentifier(RandomStringUtils.randomAlphanumeric(32));
        je.setTransactionDate(LocalDateTime.now());
        je.setState("PENDING");
        je.setTransactionType("INTR");
        je.setClerk(SecurityUtils.getCurrentUserLogin().get());
        je.setNote(bill.getBillNumber());

        if (!bill.getBillItems().isEmpty()) {

            //need to determine
            bill.getBillItems()
                    .stream()
                    .forEach(item -> {

                    });

        }

        final Debtor cashDebtor = new Debtor();
        cashDebtor.setAccountNumber("account to debit");
        cashDebtor.setAmount(roundedAmount);
        je.setDebtors(Sets.newHashSet(cashDebtor));

        final Creditor accrueCreditor = new Creditor();
        accrueCreditor.setAccountNumber("account to credit");
        accrueCreditor.setAmount(roundedAmount);
        je.setCreditors(Sets.newHashSet(accrueCreditor));

        return je;
    }

    public JournalEntry createJournalEntry(Bill bill) {
        final String roundedAmount = BigDecimal.valueOf(6500D).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString();

        final JournalEntry cashToAccrueJournalEntry = new JournalEntry();
        cashToAccrueJournalEntry.setTransactionIdentifier(RandomStringUtils.randomAlphanumeric(32));
        cashToAccrueJournalEntry.setTransactionDate(LocalDateTime.now());
        cashToAccrueJournalEntry.setState("PENDING");
        cashToAccrueJournalEntry.setTransactionType("INTR");
        cashToAccrueJournalEntry.setClerk(SecurityUtils.getCurrentUserLogin().get());
        cashToAccrueJournalEntry.setNote("Patient Billing : patient no. ");

        final Debtor cashDebtor = new Debtor();
        cashDebtor.setAccountNumber("account to debit");
        cashDebtor.setAmount(roundedAmount);
        cashToAccrueJournalEntry.setDebtors(Sets.newHashSet(cashDebtor));

        final Creditor accrueCreditor = new Creditor();
        accrueCreditor.setAccountNumber("account to credit");
        accrueCreditor.setAmount(roundedAmount);
        cashToAccrueJournalEntry.setCreditors(Sets.newHashSet(accrueCreditor));

        return cashToAccrueJournalEntry;
    }

    //posting rules
    /*
    // Service point have Income and Expense accounts
    // Receipting Point/ Cash Drawer  has Income and Expenses
   I can use this determine the posting rules
    
     */
}
