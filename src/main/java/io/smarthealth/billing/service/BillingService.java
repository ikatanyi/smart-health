package io.smarthealth.billing.service;

import com.google.common.collect.Sets;
import io.smarthealth.accounting.acc.data.v1.JournalEntry;
import io.smarthealth.billing.data.BillData;
import io.smarthealth.billing.data.BillItemData;
import io.smarthealth.billing.domain.Bill;
import io.smarthealth.billing.domain.BillItem;
import io.smarthealth.billing.domain.enumeration.BillStatus;
import io.smarthealth.billing.domain.specification.BillingSpecification;
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
import io.smarthealth.billing.domain.BillRepository;
import io.smarthealth.config.JmsMessageService;

/**
 *
 * @author Kelsas
 */
@Service
public class BillingService {

    private final BillRepository patientBillRepository;
    private final VisitService visitService;
    private final ItemService itemService;
  
    private final SequenceService sequenceService;
    private final JmsMessageService messageService;

    public BillingService(BillRepository patientBillRepository, VisitService visitService, ItemService itemService, SequenceService sequenceService, JmsMessageService messageService) {
        this.patientBillRepository = patientBillRepository;
        this.visitService = visitService;
        this.itemService = itemService;
        this.sequenceService = sequenceService;
        this.messageService = messageService;
    }
 
    @Transactional
    public Bill createPatientBill(BillData data) {
        //check the validity of the patient visit
        Visit visit = visitService.findVisitEntityOrThrow(data.getVisitNumber());
        String billNumber = sequenceService.nextNumber(SequenceType.BillNumber);

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
        patientbill.setReferenceNumber(data.getReferenceNumber());
        patientbill.setStatus(BillStatus.Draft);

        List<BillItem> lineItems = data.getBillItems()
                .stream()
                .map(lineData -> {
                    BillItem billItem = new BillItem();

                    billItem.setBillingDate(lineData.getBillingDate());
                    billItem.setTransactionNo(lineData.getTransactionNo());

                    if (lineData.getItemId() != null) {
                        Item item = itemService.findItemEntityOrThrow(lineData.getItemId());
                        billItem.setItem(item);

                    }

                    billItem.setPrice(lineData.getPrice());
                    billItem.setQuantity(lineData.getQuantity());
                    billItem.setAmount(lineData.getAmount());
                    billItem.setDiscount(lineData.getDiscount());
                    billItem.setBalance(lineData.getAmount());
                    billItem.setServicePointId(lineData.getServicePointId());
                    billItem.setServicePoint(lineData.getServicePoint());
                    billItem.setStatus(BillStatus.Draft);

                    return billItem;
                })
                .collect(Collectors.toList());
        patientbill.addBillItems(lineItems);

        Bill savedBill = patientBillRepository.save(patientbill);
        
        messageService.publish("Billing has been received Queued ... "+savedBill.getBillNumber());
        
        return savedBill;
    }

    public Optional<Bill> findByBillNumber(final String billNumber) {
        return patientBillRepository.findByBillNumber(billNumber);
    }

    public Bill findOneWithNoFoundDetection(Long id) {
        return patientBillRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Patien Bill with Id {0} not found", id));
    }

    public String addPatientBillItems(Long id, List<BillItemData> billItems) {
        Bill patientbill = findOneWithNoFoundDetection(id);
        List<BillItem> lineItems = billItems
                .stream()
                .map(lineData -> {
                    BillItem billLine = new BillItem();

                    billLine.setBillingDate(lineData.getBillingDate());
                    billLine.setTransactionNo(lineData.getTransactionNo());

                    if (lineData.getItemId() != null) {
                        Item item = itemService.findItemEntityOrThrow(lineData.getItemId());
                        billLine.setItem(item);

                    }

                    billLine.setPrice(lineData.getPrice());
                    billLine.setQuantity(lineData.getQuantity());
                    billLine.setAmount(lineData.getAmount());
                    billLine.setDiscount(lineData.getDiscount());
                    billLine.setBalance(lineData.getAmount());
                    billLine.setServicePointId(lineData.getServicePointId());
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

    private JournalEntry post(Bill patientBill) {
        
        //Note to self - aggregated service based on billing point
        // Arraylist accruedValues =new Arraylist();
        // BigDecimal.valueOf(accruedValues.parallelStream().reduce(0.00D, Double::sum)).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString();
        
        final String roundedAmount = BigDecimal.valueOf(6500D).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString();
        final io.smarthealth.accounting.acc.data.v1.JournalEntry cashToAccrueJournalEntry = new io.smarthealth.accounting.acc.data.v1.JournalEntry();
        cashToAccrueJournalEntry.setTransactionIdentifier(RandomStringUtils.randomAlphanumeric(32));
        cashToAccrueJournalEntry.setTransactionDate(LocalDateTime.now());
        cashToAccrueJournalEntry.setTransactionType("INTR");
        cashToAccrueJournalEntry.setClerk(SecurityUtils.getCurrentUserLogin().get());
        cashToAccrueJournalEntry.setNote("Patient Billing : patient no. .");

        final io.smarthealth.accounting.acc.data.v1.Debtor cashDebtor = new io.smarthealth.accounting.acc.data.v1.Debtor();
        cashDebtor.setAccountNumber("account to debit");
        cashDebtor.setAmount(roundedAmount);
        cashToAccrueJournalEntry.setDebtors(Sets.newHashSet(cashDebtor));

        final io.smarthealth.accounting.acc.data.v1.Creditor accrueCreditor = new io.smarthealth.accounting.acc.data.v1.Creditor();
        accrueCreditor.setAccountNumber("account to credit");
        accrueCreditor.setAmount(roundedAmount);
        cashToAccrueJournalEntry.setCreditors(Sets.newHashSet(accrueCreditor));

        return cashToAccrueJournalEntry;
    }
}
