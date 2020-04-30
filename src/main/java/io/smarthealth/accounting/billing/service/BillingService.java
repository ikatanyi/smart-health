package io.smarthealth.accounting.billing.service;

import io.smarthealth.accounting.accounts.data.FinancialActivity;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccount;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccountRepository;
import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.accounting.billing.data.BillItemData;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.data.PatientBillGroup;
import io.smarthealth.accounting.billing.data.SummaryBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.domain.specification.BillingSpecification;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import io.smarthealth.accounting.billing.domain.PatientBillItemRepository;
import lombok.RequiredArgsConstructor;
import io.smarthealth.accounting.billing.domain.PatientBillRepository;
import io.smarthealth.accounting.billing.domain.specification.BillSpecificationsBuilder;
import io.smarthealth.accounting.billing.domain.specification.PatientBillSpecification;
import io.smarthealth.accounting.doctors.domain.DoctorInvoice;
import io.smarthealth.accounting.doctors.domain.DoctorItem;
import io.smarthealth.accounting.doctors.service.DoctorInvoiceService;
import io.smarthealth.accounting.payment.data.BilledItem;
import io.smarthealth.accounting.payment.data.ReceivePayment;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.domain.SearchOperation;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.stores.domain.Store;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {
    
    private final ServicePointService servicePointService;
    private final PatientBillRepository patientBillRepository;
    private final PatientBillItemRepository billItemRepository;
    private final VisitRepository visitRepository;
    private final ItemService itemService;
    private final JournalService journalService;
    private final SequenceNumberService sequenceNumberService;
    private final FinancialActivityAccountRepository activityAccountRepository;
    private final DoctorInvoiceService doctorInvoiceService;
    private final CashPaidUpdater cashPaidUpdater;

    //Create service bill
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PatientBill createPatientBill(BillData data) {
        System.out.println("About to create bill ");
        PatientBill patientbill = new PatientBill();
        if (!data.getWalkinFlag()) {
            Visit visit = findVisitEntityOrThrow(data.getVisitNumber());
            patientbill.setVisit(visit);
            patientbill.setPatient(visit.getPatient());
            patientbill.setWalkinFlag(Boolean.FALSE);
        } else {
            patientbill.setReference(data.getPatientNumber());
            patientbill.setOtherDetails(data.getPatientName());
            patientbill.setWalkinFlag(Boolean.TRUE);
        }
        
        patientbill.setAmount(data.getAmount());
        patientbill.setDiscount(data.getDiscount());
        patientbill.setBalance(data.getAmount());
        
        patientbill.setBillingDate(data.getBillingDate());
        patientbill.setPaymentMode(data.getPaymentMode());
        
        patientbill.setStatus(BillStatus.Draft);
        
        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String bill_no = sequenceNumberService.next(1L, Sequences.BillNumber.name());
        
        patientbill.setBillNumber(bill_no);
        patientbill.setTransactionId(trdId);
        
        List<PatientBillItem> lineItems = data.getBillItems()
                .stream()
                .map(lineData -> {
                    Item item = itemService.findItemWithNoFoundDetection(lineData.getItemCode());
                    PatientBillItem billItem = new PatientBillItem();
                    
                    billItem.setBillingDate(lineData.getBillingDate());
                    billItem.setTransactionId(trdId);
                    billItem.setItem(item);
                    billItem.setPaid(data.getPaymentMode().equals("Insurance"));
                    billItem.setPrice(lineData.getPrice());
                    if (item.getCategory().equals(ItemCategory.CoPay)) {
                        billItem.setPrice(data.getAmount());
                    }
                    billItem.setQuantity(lineData.getQuantity());
                    billItem.setAmount(lineData.getAmount());
                    billItem.setDiscount(lineData.getDiscount());
                    billItem.setBalance(lineData.getAmount());
                    billItem.setServicePoint(lineData.getServicePoint());
                    billItem.setServicePointId(lineData.getServicePointId());
                    billItem.setStatus(BillStatus.Draft);
                    billItem.setMedicId(lineData.getMedicId());
                    return billItem;
                })
                .collect(Collectors.toList());
        patientbill.addBillItems(lineItems);
        System.out.println("End of bill items");
        
        PatientBill savedBill = save(patientbill);
        log.info("START save to journal");
        if (savedBill.getPaymentMode().equals("Insurance")) {
            journalService.save(toJournal(savedBill, null));
        }
        log.info("END create patient bill");
        return savedBill;
    }
    
    public void createPatientBill(PatientBill bill, Store store) {
        String bill_no = sequenceNumberService.next(1L, Sequences.BillNumber.name());
        bill.setBillNumber(bill_no);
        PatientBill savedBill = save(bill);
        if (savedBill.getPaymentMode().equals("Insurance")) {
            journalService.save(toJournal(savedBill, store));
        }
    }
    
    public PatientBill save(PatientBill bill) {
        log.info("START save bill");
        String bill_no = bill.getBillNumber() == null ? sequenceNumberService.next(1L, Sequences.BillNumber.name()) : bill.getBillNumber();
        String trdId = bill.getTransactionId() == null ? sequenceNumberService.next(1L, Sequences.Transactions.name()) : bill.getTransactionId();
        bill.setBillNumber(bill_no);
        bill.setTransactionId(trdId);
        
        PatientBill savedBill = patientBillRepository.saveAndFlush(bill);
        
        if (bill.getWalkinFlag() == null || bill.getWalkinFlag()) {
            return savedBill;
        }
        List<DoctorInvoice> doctorInvoices = toDoctorInvoice(savedBill);
        if (doctorInvoices.size() > 0) {
            doctorInvoices.forEach(inv -> doctorInvoiceService.save(inv));
        }
        log.info("END save bill");
        return savedBill;
    }
    
    public PatientBill update(PatientBill bill) {
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
        //determine the request origin and update ti
        cashPaidUpdater.updateRequestStatus(item);
        return billItemRepository.save(item);
    }
    
    public Item getItemByCode(String code) {
        return itemService.findByItemCodeOrThrow(code);
    }
    
    public Item getItemByBy(Long id) {
        return itemService.findById(id)
                .orElseThrow(() -> APIException.notFound("Service with Item Id {0} Not Found", id));
    }
    
    public String addPatientBillItems(Long id, List<BillItemData> billItems) {
        PatientBill patientbill = findOneWithNoFoundDetection(id);
        List<PatientBillItem> lineItems = billItems
                .stream()
                .map(lineData -> {
                    Item item = itemService.findByItemCodeOrThrow(lineData.getItemCode());
                    PatientBillItem billLine = new PatientBillItem();
                    
                    billLine.setBillingDate(lineData.getBillingDate());
                    billLine.setTransactionId(lineData.getTransactionId());
                    billLine.setItem(item);
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
    
    @Deprecated
    public Page<PatientBill> findAllBills(String transactionNo, String visitNo, String patientNo, String paymentMode, String billNo, BillStatus status, DateRange range, Pageable page) {
        
        Specification<PatientBill> spec = BillingSpecification.createSpecification(transactionNo, visitNo, patientNo, paymentMode, billNo, status, range);
        
        return patientBillRepository.findAll(spec, page);
        
    }
    
    public Page<PatientBillItem> getPatientBillItems(String patientNo, String visitNo, String billNumber, String transactionId, Long servicePointId, Boolean hasBalance, BillStatus status, DateRange range, Pageable page) {
        Specification<PatientBillItem> spec = PatientBillSpecification.createSpecification(patientNo, visitNo, billNumber, transactionId, servicePointId, hasBalance, status, range);
        return billItemRepository.findAll(spec, page);
    }
    
    public Page<SummaryBill> getSummaryBill(String visitNumber, String patientNumber, Boolean hasBalance, DateRange range, Pageable pageable) {
        return billItemRepository.getBillSummary(visitNumber, patientNumber, hasBalance, range, pageable);
    }
    
    public Page<SummaryBill> getWalkinSummaryBill(String patientNumber, Boolean hasBalance, Pageable pageable) {
        return billItemRepository.getWalkinBillSummary(patientNumber, hasBalance, pageable);
    }
    
    public Page<PatientBillItem> getWalkBillItems(String walkIn, Boolean hasBalance, Pageable page) {
        Specification<PatientBillItem> spec = PatientBillSpecification.getWalkinBillItems(walkIn, hasBalance);
        return billItemRepository.findAll(spec, page);
    }
    
    public Visit findVisitEntityOrThrow(String visitNumber) {
        return this.visitRepository.findByVisitNumber(visitNumber)
                .orElseThrow(() -> APIException.notFound("Visit Number {0} not found.", visitNumber));
    }
    
    public List<PatientBillGroup> getPatientBillGroups(BillStatus status) {
        return patientBillRepository.groupBy(status);
    }
    
    @Deprecated
    public Page<PatientBillItem> getPatientBillItemByVisit(String visitNumber, Pageable page) {
        Visit visit = findVisitEntityOrThrow(visitNumber);
        
        return billItemRepository.findPatientBillItemByVisit(visit, page);
    }
    
    @Deprecated
    public List<BillData> withBalances() {
        return patientBillRepository.findAll(billHasBalance()).stream().map(x -> x.toData()).collect(Collectors.toList());
    }
    
    @Deprecated
    private Specification<PatientBill> billHasBalance() {
        return (Root<PatientBill> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            return cb.greaterThan(root.get("balance"), 0);
        };
    }
    
    private JournalEntry toJournal(PatientBill bill, Store store) {
        String description = "Patient Billing";
        if (bill.getPatient() != null) {
            Patient patient = bill.getPatient();
            description = patient.getPatientNumber() + " " + patient.getFullName();
        }
        
        Optional<FinancialActivityAccount> debitAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Patient_Control);
        if (!debitAccount.isPresent()) {
            throw APIException.badRequest("Patient Control Account is Not Mapped");
        }
//        String debitAcc = debitAccount.get().getAccount().getIdentifier();
        List<JournalEntryItem> items = new ArrayList<>();
        
        if (!bill.getBillItems().isEmpty()) {
            Map<Long, Double> map = bill.getBillItems()
                    .stream()
                    .collect(Collectors.groupingBy(PatientBillItem::getServicePointId,
                            Collectors.summingDouble(PatientBillItem::getAmount)
                    )
                    );
            //then here since we making a revenue
            map.forEach((k, v) -> {
                
                ServicePoint srv = servicePointService.getServicePoint(k);
                String desc = srv.getName() + " Patient Billing";
                Account credit = srv.getIncomeAccount();
                BigDecimal amount = BigDecimal.valueOf(v);
                
                items.add(new JournalEntryItem(debitAccount.get().getAccount(), desc, amount, BigDecimal.ZERO));
                items.add(new JournalEntryItem(credit, desc, BigDecimal.ZERO, amount));
                
            });
            //if inventory expenses this shit!
            if (store != null) {
                Map<Long, Double> inventory = bill.getBillItems()
                        .stream()
                        .filter(x -> x.getItem().isInventoryItem())
                        .collect(Collectors.groupingBy(PatientBillItem::getServicePointId,
                                Collectors.summingDouble(x -> (x.getItem().getCostRate().doubleValue() * x.getQuantity()))
                        )
                        );
                if (!inventory.isEmpty()) {
                    inventory.forEach((k, v) -> {
                        //revenue
                        String pat = "";
                        if (bill.getPatient() != null) {
                            pat = bill.getPatient().getPatientNumber() + " - " + bill.getPatient().getFullName();
                        }
                        //TODO                      
                        String desc = "Issuing Stocks to " + pat;
                        ServicePoint srv = servicePointService.getServicePoint(k);
                        Account debit = srv.getExpenseAccount(); // cost of sales
                        Account credit = srv.getInventoryAssetAccount();//store.getInventoryAccount(); // Inventory Asset Account
                        BigDecimal amount = BigDecimal.valueOf(v);
                        
                        items.add(new JournalEntryItem(debit, desc, amount, BigDecimal.ZERO));
                        items.add(new JournalEntryItem(credit, desc, BigDecimal.ZERO, amount));
                        
                    });
                }
                
            }
            
        }
        
        JournalEntry toSave = new JournalEntry(bill.getBillingDate(), description, items);
        toSave.setTransactionNo(bill.getTransactionId());
        toSave.setTransactionType(TransactionType.Billing);
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }
    
    private List<DoctorInvoice> toDoctorInvoice(PatientBill bill) {
        System.out.println("Line 369");
        System.out.println("toDoctorInvoice " + bill.getBillNumber());
        System.out.println("toDoctorInvoice " + bill.getVisit().getVisitNumber());
        System.out.println("toDoctorInvoice " + bill.getAmount());
        return bill.getBillItems()
                .stream()
                .map(billItem -> {
                    if (billItem.getMedicId() == null) {
                        return null;
                    }
                    Employee doctor = doctorInvoiceService.getDoctorById(billItem.getMedicId());
                    Optional<DoctorItem> doctorItem = doctorInvoiceService.getDoctorItem(doctor, billItem.getItem());
                    
                    if (doctorItem.isPresent()) {
                        DoctorItem docItem = doctorItem.get();
                        if (docItem.getActive()) {
                            BigDecimal amt = computeDoctorFee(docItem);
                            DoctorInvoice invoice = new DoctorInvoice();
                            invoice.setAmount(amt);
                            invoice.setBalance(amt);
                            invoice.setDoctor(doctor);
                            invoice.setInvoiceDate(billItem.getBillingDate());
                            invoice.setInvoiceNumber(billItem.getPatientBill().getBillNumber());
                            invoice.setBillItemId(billItem.getId());
                            invoice.setPaid(Boolean.FALSE);
                            invoice.setPatient(billItem.getPatientBill().getPatient());
                            invoice.setPaymentMode(billItem.getPatientBill().getPaymentMode());
                            invoice.setServiceItem(docItem);
                            invoice.setTransactionId(billItem.getTransactionId());
                            System.out.println("billItem.getPatientBill().getVisit() " + billItem.getPatientBill().getVisit().getVisitNumber());
                            invoice.setVisit(billItem.getPatientBill().getVisit());
                            return invoice;
                        }
                    }
                    return null;
                })
                .filter(x -> x != null)
                .collect(Collectors.toList());
    }
    
    private BigDecimal computeDoctorFee(DoctorItem item) {
        if (item.getIsPercentage()) {
            BigDecimal doctorRate = item.getAmount().divide(BigDecimal.valueOf(100)).multiply(item.getServiceType().getRate());
            return doctorRate;
        }
        return item.getAmount();
    }
    
    public List<PatientBill> search(String search) {
        BillSpecificationsBuilder builder = new BillSpecificationsBuilder();
        
        String operationSetExper = StringUtils.join(SearchOperation.SIMPLE_OPERATION_SET, "|");
        Pattern pattern = Pattern.compile("(\\w+?)(" + operationSetExper + ")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(4), matcher.group(3), matcher.group(5));
        }
        
        Specification<PatientBill> spec = builder.build();
        return patientBillRepository.findAll(spec);
    }
    
    @Transactional
    public List<PatientBillItem> validatedBilledItem(ReceivePayment data) {
        
        List<PatientBillItem> receiptingBills = new ArrayList<>();
        List<PatientBillItem> toCreate = new ArrayList<>();
        
        if (!data.getBillItems().isEmpty()) {
            data.getBillItems().stream()
                    .forEach(x -> {
                        if (x.getBillItemId() != null) {
                            PatientBillItem item = findBillItemById(x.getBillItemId());
                            BigDecimal bal = BigDecimal.valueOf(item.getAmount()).subtract(x.getAmount());
                            item.setPaid(Boolean.TRUE);
                            item.setStatus(BillStatus.Paid);
                            if (item.getItem().getCategory() == ItemCategory.CoPay) {
                                item.setAmount((item.getAmount() * -1));
                            }
                            item.setPaymentReference(data.getReceiptNo());
                            item.setBalance(bal.doubleValue());
                            PatientBillItem i = updateBillItem(item);
                            
                            receiptingBills.add(i);
                            
                        } else {
                            PatientBillItem item = createReciptItem(x);
                            if (item.getItem().getCategory() == ItemCategory.CoPay) {
                                item.setAmount((item.getAmount() * -1));
                            }
                            item.setMedicId(x.getMedicId());
                            item.setTransactionId(data.getTransactionNo());
                            item.setPaymentReference(data.getReceiptNo());
                            toCreate.add(item);
                        }
                    });
        }
        if (!toCreate.isEmpty()) {
            PatientBill patientbill = new PatientBill();
            if (data.getWalkin() != null && !data.getWalkin()) {
                Visit visit = findVisitEntityOrThrow(data.getVisitNumber());
                patientbill.setVisit(visit);
                patientbill.setPatient(visit.getPatient());
                patientbill.setWalkinFlag(Boolean.FALSE);
            } else {
                patientbill.setReference(data.getPayerNumber());
                patientbill.setOtherDetails(data.getPayer());
                patientbill.setWalkinFlag(Boolean.TRUE);
            }
            Double amount = toCreate.stream()
                    .collect(Collectors.summingDouble(x -> x.getAmount()));
            
            patientbill.setAmount(amount);
            patientbill.setDiscount(0D);
            patientbill.setBalance(0D);
            
            patientbill.setBillingDate(LocalDate.now());
            patientbill.setPaymentMode("Cash");
            
            patientbill.setStatus(BillStatus.Paid);
            
            String bill_no = sequenceNumberService.next(1L, Sequences.BillNumber.name());
            
            patientbill.setBillNumber(bill_no);
            patientbill.setTransactionId(data.getTransactionNo());
            patientbill.addBillItems(toCreate);
            
            PatientBill savedBill = save(patientbill);
            
            receiptingBills.addAll(savedBill.getBillItems());
            
        }
        
        return receiptingBills;
    }
    
    private PatientBillItem createReciptItem(BilledItem billedItem) {
        Item item = getItemByBy(billedItem.getPricelistItemId());
        
        PatientBillItem savedItem = new PatientBillItem();
        savedItem.setPrice(billedItem.getPrice());
        savedItem.setQuantity(billedItem.getQuantity());
        savedItem.setAmount(billedItem.getAmount().doubleValue());
        savedItem.setBalance(billedItem.getAmount().doubleValue());
        savedItem.setBillingDate(LocalDate.now());
        savedItem.setDiscount(0D);
        savedItem.setTaxes(0D);
        savedItem.setItem(item);
        savedItem.setPaid(Boolean.TRUE);
        savedItem.setStatus(BillStatus.Paid);
        savedItem.setBalance(0D);
        savedItem.setServicePoint(billedItem.getServicePoint());
        savedItem.setServicePointId(billedItem.getServicePointId());
        
        return savedItem;
    }
}
