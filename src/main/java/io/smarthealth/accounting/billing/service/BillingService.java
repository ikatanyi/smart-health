package io.smarthealth.accounting.billing.service;

import io.smarthealth.accounting.acc.data.v1.Creditor;
import io.smarthealth.accounting.acc.data.v1.Debtor;
import io.smarthealth.accounting.acc.data.v1.FinancialActivity;
import io.smarthealth.accounting.acc.data.v1.JournalEntry;
import io.smarthealth.accounting.acc.domain.AccountEntity;
import io.smarthealth.accounting.acc.domain.FinancialActivityAccount;
import io.smarthealth.accounting.acc.domain.FinancialActivityAccountRepository;
import io.smarthealth.accounting.acc.service.JournalEntryService;
import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.accounting.billing.data.BillItemData;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.domain.specification.BillingSpecification;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.SecurityUtils;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.accounting.billing.domain.PatientBillItemRepository;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.pharmacy.data.PharmacyData;
import io.smarthealth.infrastructure.numbers.service.SequenceNumberGenerator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import io.smarthealth.accounting.billing.domain.PatientBillRepository;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BillingService {

    private final PatientBillRepository patientBillRepository;
    private final PatientBillItemRepository billItemRepository;
    private final VisitService visitService;
    private final ItemService itemService;
    private final SequenceNumberGenerator sequenceGenerator;
//    private final TxnService txnService;
    private final JournalEntryService journalService;
    private final ServicePointService servicePointService;
    private final StoreService storeService;
    private final FinancialActivityAccountRepository activityAccountRepository;

    public PatientBill createBill(BillData data) {
        //check the validity of the patient visit
        Visit visit = visitService.findVisitEntityOrThrow(data.getVisitNumber());
//        String billNumber = RandomStringUtils.randomNumeric(6); //sequenceService.nextNumber(SequenceType.BillNumber);
        String trdId = sequenceGenerator.generateTransactionNumber();

        PatientBill patientbill = new PatientBill();
        patientbill.setVisit(visit);
        patientbill.setPatient(visit.getPatient());
        patientbill.setAmount(data.getAmount());
        patientbill.setDiscount(data.getDiscount());
        patientbill.setBalance(data.getAmount());
//        patientbill.setBillNumber(billNumber);
        patientbill.setBillingDate(data.getBillingDate());
        patientbill.setReferenceNo(data.getReferenceNo());
        patientbill.setPaymentMode(data.getPaymentMode());
        patientbill.setTransactionId(trdId);
        patientbill.setStatus(BillStatus.Draft);

        List<PatientBillItem> lineItems = data.getBillItems()
                .stream()
                .map(lineData -> {
                    PatientBillItem billItem = new PatientBillItem();

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
                    billItem.setServicePointId(lineData.getServicePointId());
                    billItem.setStatus(BillStatus.Draft);

                    return billItem;
                })
                .collect(Collectors.toList());
        patientbill.addBillItems(lineItems);

        PatientBill savedBill = save(patientbill);
        savedBill.setBillNumber(sequenceGenerator.generate(savedBill));
        save(savedBill);

        journalService.createJournalEntry(savedBill);

//        journalService.createJournalEntry(toJournal(trdId, savedBill));
        //trigger stock balance if items is an inventory
//         journalSender.postJournal(toJournal(savedBill)); 
        return savedBill;
    }

    public PatientBill createBill(Store store, PharmacyData data) {
        //check the validity of the patient visit
        Visit visit = visitService.findVisitEntityOrThrow(data.getVisitNumber());
       
        PatientBill patientbill = new PatientBill();
        patientbill.setVisit(visit);
        patientbill.setPatient(visit.getPatient());
        patientbill.setAmount(data.getAmount());
        patientbill.setDiscount(data.getDiscount());
        patientbill.setBalance(data.getAmount());
        patientbill.setBillingDate(data.getDispenseDate());
        patientbill.setReferenceNo(data.getReferenceNo());
        patientbill.setPaymentMode(data.getPaymentMode());
        patientbill.setTransactionId(data.getTransactionId());
        patientbill.setStatus(BillStatus.Draft);

        List<PatientBillItem> lineItems = data.getDrugItems()
                .stream()
                .map(lineData -> {
                    PatientBillItem billItem = new PatientBillItem();

                    billItem.setBillingDate(lineData.getBillingDate());
                    billItem.setTransactionId(data.getTransactionId());

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
                    billItem.setServicePointId(lineData.getServicePointId());
                    billItem.setStatus(BillStatus.Draft);

                    return billItem;
                })
                .collect(Collectors.toList());
        patientbill.addBillItems(lineItems);

        PatientBill savedBill = save(patientbill);
        savedBill.setBillNumber(sequenceGenerator.generate(savedBill));
        save(savedBill);

//        journalService.createJournalEntry(toJournal(trdId, savedBill)); 
        journalService.createJournalEntry(savedBill, store);
        //trigger stock balance if items is an inventory
//         journalSender.postJournal(toJournal(savedBill)); 
        return savedBill;
    }

    public PatientBill save(PatientBill bill) {
        return patientBillRepository.save(bill);
    }

    public Optional<PatientBill> findByBillNumber(final String billNumber) {
        return patientBillRepository.findByBillNumber(billNumber);
    }

    public PatientBill findOneWithNoFoundDetection(Long id) {
        return patientBillRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Bill with Id {0} not found", id));
    }

    public PatientBillItem findBillItemById(Long id) {
        return billItemRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Bill Item with Id {0} not found", id));
    }

    public PatientBillItem updateBillItem(PatientBillItem item) {
        return billItemRepository.save(item);
    }

    public Item getItemByCode(String code) {
        return itemService.findByItemCodeOrThrow(code);
    }

    public String addPatientBillItems(Long id, List<BillItemData> billItems) {
        PatientBill patientbill = findOneWithNoFoundDetection(id);
        List<PatientBillItem> lineItems = billItems
                .stream()
                .map(lineData -> {
                    PatientBillItem billLine = new PatientBillItem();

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
                    billLine.setServicePointId(lineData.getServicePointId());
                    billLine.setStatus(BillStatus.Draft);

                    return billLine;
                })
                .collect(Collectors.toList());
        patientbill.addBillItems(lineItems);

        return patientbill.getBillNumber();
    }

    public Page<PatientBill> findAllBills(String transactionNo, String visitNo, String patientNo, String paymentMode, String billNo, String status, Pageable page) {
        BillStatus state = BillStatus.valueOf(status);
        Specification<PatientBill> spec = BillingSpecification.createSpecification(transactionNo, visitNo, patientNo, paymentMode, billNo, state);

        return patientBillRepository.findAll(spec, page);

    }

//    private JournalEntry toJournal(String trxId, PatientBill bill) {
////        final String roundedAmount = BigDecimal.valueOf(6500D).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString();
//
//        Optional<FinancialActivityAccount> debitAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Patient_Invoice_Control);
//
//        if (debitAccount.isPresent()) {
//            String debitAcc = debitAccount.get().getAccount().getIdentifier();
//            final JournalEntry je = new JournalEntry();
//            je.setTransactionDate(LocalDateTime.now());
//            je.setState("PENDING");;
//            je.setTransactionNo(trxId);
//            je.setTransactionType("Billing");
//            je.setClerk(SecurityUtils.getCurrentUserLogin().get());
//            je.setNote(bill.getBillNumber());
//
//            Set<Creditor> creditors = new HashSet<>();
//            Set<Debtor> debtors = new HashSet<>();
//
//            if (!bill.getBillItems().isEmpty()) {
//                Map<Long, Double> map = bill.getBillItems()
//                        .stream()
//                        .collect(Collectors.groupingBy(PatientBillItem::getServicePointId,
//                                Collectors.summingDouble(PatientBillItem::getAmount)
//                        )
//                        );
//
//                //then here since we making a revenue
//                map.forEach((k, v) -> {
//                    ServicePoint srv = servicePointService.getServicePoint(k);
//                    AccountEntity credit = srv.getIncomeAccount();
//                    String amount = roundedAmount(v);
//                    debtors.add(new Debtor(debitAcc, amount));
//                    creditors.add(new Creditor(credit.getIdentifier(), amount));
//                    //expense Inventory
//
//                });
//
//                bill.getBillItems()
//                        .stream()
//                        .forEach(item -> {
//
//                        });
//
//            }
//
//            je.setCreditors(creditors);
//            je.setDebtors(debtors);
//
////            final Debtor cashDebtor = new Debtor();
////            cashDebtor.setAccountNumber("account to debit");
////            cashDebtor.setAmount(roundedAmount);
////            je.setDebtors(Sets.newHashSet(cashDebtor));
////
////            final Creditor accrueCreditor = new Creditor();
////            accrueCreditor.setAccountNumber("account to credit");
////            accrueCreditor.setAmount(roundedAmount);
////            je.setCreditors(Sets.newHashSet(accrueCreditor));
//            return je;
//        } else {
//            throw APIException.badRequest("Patient Control Account is Not Mapped");
//        }
//    }
    private String roundedAmount(Double amt) {
        return BigDecimal.valueOf(amt)
                .setScale(2, BigDecimal.ROUND_HALF_EVEN)
                .toString();
    }

}
