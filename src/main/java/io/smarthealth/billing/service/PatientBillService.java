package io.smarthealth.billing.service;

import io.smarthealth.accounting.account.domain.Account;
import io.smarthealth.accounting.account.domain.FinancialActivityAccount;
import io.smarthealth.accounting.account.domain.Journal;
import io.smarthealth.accounting.account.domain.JournalEntry;
import io.smarthealth.accounting.account.domain.enumeration.FinancialActivity;
import io.smarthealth.accounting.account.domain.enumeration.JournalState;
import io.smarthealth.accounting.account.domain.enumeration.TransactionType;
import io.smarthealth.accounting.account.service.FinancialActivityAccountService;
import io.smarthealth.accounting.account.service.JournalService;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.billing.data.PatientBillData;
import io.smarthealth.billing.data.PatientBillItemData;
import io.smarthealth.billing.domain.PatientBill;
import io.smarthealth.billing.domain.PatientBillItem;
import io.smarthealth.billing.domain.PatientBillRepository;
import io.smarthealth.billing.domain.enumeration.BillStatus;
import io.smarthealth.billing.domain.specification.BillingSpecification; 
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.infrastructure.sequence.service.SequenceService;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
public class PatientBillService {

    private final PatientBillRepository patientBillRepository;
    private final VisitService visitService;
    private final ItemService itemService;
    private final FinancialActivityAccountService financialActivityAccountService;
    private final JournalService journalService;
    private final ServicePointService pointsService;
    private final SequenceService sequenceService;

    public PatientBillService(PatientBillRepository patientBillRepository,
            VisitService visitService,
            ItemService itemService,
            FinancialActivityAccountService financialActivityAccountService,
            JournalService journalService,
            SequenceService sequenceService,
            ServicePointService pointsService) {

        this.patientBillRepository = patientBillRepository;
        this.visitService = visitService;
        this.itemService = itemService;
        this.financialActivityAccountService = financialActivityAccountService;
        this.journalService = journalService;
        this.sequenceService = sequenceService;
        this.pointsService = pointsService;
    }

    @Transactional
    public PatientBill createPatientBill(PatientBillData data) {
        //check the validity of the patient visit
        Visit visit = visitService.findVisitEntityOrThrow(data.getVisitNumber());
        String billNumber = sequenceService.nextNumber(SequenceType.BillNumber);

        PatientBill patientbill = new PatientBill();
        patientbill.setVisit(visit);
        patientbill.setPatient(visit.getPatient());
        patientbill.setAmount(data.getAmount());
        patientbill.setBalance(data.getAmount());
        patientbill.setBillNumber(billNumber);
        patientbill.setBillingDate(data.getBillingDate());
        patientbill.setJournalNumber(data.getJournalNumber());
        patientbill.setPaymentMode(data.getPaymentMode());
        patientbill.setReferenceNumber(data.getReferenceNumber());
        patientbill.setStatus(BillStatus.Draft);

        List<PatientBillItem> lineItems = data.getBillItems()
                .stream()
                .map(lineData -> {
                    PatientBillItem billLine = new PatientBillItem();

                    billLine.setBillingDate(lineData.getBillingDate());
                    billLine.setTransactionNo(lineData.getTransactionNo());

                    if (lineData.getItemId() != null) {
                        Item item = itemService.findItemEntityOrThrow(lineData.getItemId());
                        billLine.setItem(item);

                    }

                    billLine.setPrice(lineData.getPrice());
                    billLine.setQuantity(lineData.getQuantity());
                    billLine.setAmount(lineData.getAmount());
                    billLine.setBalance(lineData.getAmount());
                    billLine.setServicePointId(lineData.getServicePointId());
                    billLine.setServicePoint(lineData.getServicePoint());
                    billLine.setStatus(BillStatus.Draft);

                    return billLine;
                })
                .collect(Collectors.toList());
        patientbill.addBillItems(lineItems);

        PatientBill savedBill = patientBillRepository.save(patientbill);
        createJournal(savedBill);

        return savedBill;
    }

    public Optional<PatientBill> findByBillNumber(final String billNumber) {
        return patientBillRepository.findByBillNumber(billNumber);
    }

    public PatientBill findOneWithNoFoundDetection(Long id) {
        return patientBillRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Patien Bill with Id {0} not found", id));
    }

    public String addPatientBillItems(Long id, List<PatientBillItemData> billItems) {
        PatientBill patientbill = findOneWithNoFoundDetection(id);
        List<PatientBillItem> lineItems = billItems
                .stream()
                .map(lineData -> {
                    PatientBillItem billLine = new PatientBillItem();

                    billLine.setBillingDate(lineData.getBillingDate());
                    billLine.setTransactionNo(lineData.getTransactionNo());

                    if (lineData.getItemId() != null) {
                        Item item = itemService.findItemEntityOrThrow(lineData.getItemId());
                        billLine.setItem(item);

                    }

                    billLine.setPrice(lineData.getPrice());
                    billLine.setQuantity(lineData.getQuantity());
                    billLine.setAmount(lineData.getAmount());
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

    public Page<PatientBill> findAllBills(String refNo, String visitNo, String patientNo, String paymentMode, String billNo, String status, Pageable page) {
        BillStatus state = BillStatus.valueOf(status);
        Specification<PatientBill> spec = BillingSpecification.createSpecification(refNo, visitNo, patientNo, paymentMode, billNo, state);

        return patientBillRepository.findAll(spec, page);

    }

    private void createJournal(PatientBill bill) {
        FinancialActivityAccount activity = financialActivityAccountService.getByTransactionType(FinancialActivity.Patient_Invoice_Control)
                .orElseThrow(() -> APIException.badRequest("Patient Control Account for billing is not mapped"));

        Account patientControlAccount = activity.getAccount();

        Journal journal = new Journal();
        journal.setTransactionType(TransactionType.Billing);
        journal.setDocumentDate(bill.getBillingDate());
        journal.setManualEntry(false);
        journal.setReferenceNumber(bill.getReferenceNumber());
        journal.setState(JournalState.APPROVED);
        journal.setTransactionDate(bill.getBillingDate());
        journal.setActivity(bill.getPatient().getPatientNumber());

        bill.getBillLines()
                .forEach(billItem -> {
                    Account revenueAccount = pointsService.getServicePoint(billItem.getServicePointId()).getIncomeAccount();

                    journal.addJournalEntry(new JournalEntry(patientControlAccount, 0D, billItem.getAmount(), billItem.getBillingDate(), String.format("Patient Billing %s bill number %s", bill.getPatient().getPatientNumber(), bill.getBillNumber())));
                    journal.addJournalEntry(new JournalEntry(revenueAccount, billItem.getAmount(), 0D, billItem.getBillingDate(), String.format("Patient Billing %s bill number %s", bill.getPatient().getPatientNumber(), bill.getBillNumber())));

                    if (billItem.getItem().isInventoryItem()) {
                        Account expenseAccount = pointsService.getServicePoint(billItem.getServicePointId()).getIncomeAccount();
                        journal.addJournalEntry(new JournalEntry(expenseAccount, 0D, billItem.getItem().getCostRate(), billItem.getBillingDate(), "Stocks Inventory"));
                        journal.addJournalEntry(new JournalEntry(patientControlAccount, billItem.getItem().getCostRate(), 0D, billItem.getBillingDate(), "Stocks Inventory"));
                    }
                });

        journalService.createJournalEntry(journal.toData());
        //do a stock movement for the inventory at this point

    }
}
