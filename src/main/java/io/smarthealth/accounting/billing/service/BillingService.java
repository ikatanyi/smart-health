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
import io.smarthealth.accounting.billing.data.CopayData;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.data.PatientBillGroup;
import io.smarthealth.accounting.billing.data.SummaryBill;
import io.smarthealth.accounting.billing.data.nue.BillDetail;
import io.smarthealth.accounting.billing.data.nue.BillItem;
import io.smarthealth.accounting.billing.data.nue.BillPayment;
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
import io.smarthealth.accounting.billing.domain.specification.PatientBillSpecification;
import io.smarthealth.accounting.doctors.domain.DoctorInvoice;
import io.smarthealth.accounting.doctors.domain.DoctorItem;
import io.smarthealth.accounting.doctors.service.DoctorInvoiceService;
import io.smarthealth.accounting.payment.data.BilledItem;
import io.smarthealth.accounting.payment.data.ReceivePayment;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.scheme.domain.SchemeConfigurations;
import io.smarthealth.debtor.scheme.domain.enumeration.CoPayType;
import io.smarthealth.debtor.scheme.service.SchemeService;
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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
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
    private final SchemeService schemeService;
    private final ItemService itemService;
    private final JournalService journalService;
    private final SequenceNumberService sequenceNumberService;
    private final FinancialActivityAccountRepository activityAccountRepository;
    private final DoctorInvoiceService doctorInvoiceService;
    private final CashPaidUpdater cashPaidUpdater;

    //Create service bill
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PatientBill createPatientBill(BillData data) {
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

    @Transactional
    public void cancelBillItem(List<PatientBillItem> billItems) {
        billItems.stream()
                .map(x -> {
                    x.setStatus(BillStatus.Canceled);
                    return x;
                })
                .forEach(bill -> billItemRepository.save(bill));

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

        Page<PatientBillItem> lists = billItemRepository.findAll(spec, page);

        return lists;
    }

    public Page<PatientBillItem> getReceiptedBillItems(String visitNo, Pageable page) {
        Visit visit = findVisitEntityOrThrow(visitNo);
        Specification<PatientBillItem> spec = PatientBillSpecification.getReceiptedItems(visitNo, visit.getPaymentMethod());

        Page<PatientBillItem> lists = billItemRepository.findAll(spec, page);

        return lists;
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

//    @Deprecated
//    public Page<PatientBillItem> getPatientBillItemByVisit(String visitNumber, Pageable page) {
//        Visit visit = findVisitEntityOrThrow(visitNumber);
//
//        return billItemRepository.findPatientBillItemByVisit(visit, page);
//    }
//    @Deprecated
//    public List<BillData> withBalances() {
//        return patientBillRepository.findAll(billHasBalance()).stream().map(x -> x.toData()).collect(Collectors.toList());
//    }
//    @Deprecated
//    private Specification<PatientBill> billHasBalance() {
//        return (Root<PatientBill> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
//            return cb.greaterThan(root.get("balance"), 0);
//        };
//    }
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

//    public List<PatientBill> search(String search) {
//        BillSpecificationsBuilder builder = new BillSpecificationsBuilder();
//
//        String operationSetExper = StringUtils.join(SearchOperation.SIMPLE_OPERATION_SET, "|");
//        Pattern pattern = Pattern.compile("(\\w+?)(" + operationSetExper + ")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),");
//        Matcher matcher = pattern.matcher(search + ",");
//        while (matcher.find()) {
//            builder.with(matcher.group(1), matcher.group(2), matcher.group(4), matcher.group(3), matcher.group(5));
//        }
//
//        Specification<PatientBill> spec = builder.build();
//        return patientBillRepository.findAll(spec);
//    }
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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PatientBill createCopay(CopayData data) {
        String visitNumber = data.getVisitNumber();
        Long schemeId = data.getSchemeId();
        //TODO check if the visit is valid or not
        Visit visit = visitRepository.findByVisitNumberAndStatus(visitNumber, VisitEnum.Status.CheckIn)
                .orElseThrow(() -> APIException.badRequest("Visit Number {0} is not active for transaction", visitNumber));
        Scheme scheme = schemeService.fetchSchemeById(schemeId);

        Optional<SchemeConfigurations> schemeConfigs = schemeService.fetchSchemeConfigByScheme(scheme);
        if (schemeConfigs.isPresent()) {
            SchemeConfigurations config = schemeConfigs.get();

            BigDecimal copayAmount = BigDecimal.valueOf(config.getCoPayValue());

            if (config.getCoPayType() == CoPayType.Percentage) {
                if (data.getVisitStart()) {
                    return null;
                }

                BigDecimal currentBill = billItemRepository.getTotalBill(visitNumber);
                copayAmount = (copayAmount.divide(BigDecimal.valueOf(100))).multiply(currentBill);
            }
            if (copayAmount != null && copayAmount != BigDecimal.ZERO) {
                //create the bill
                Double copay = copayAmount.doubleValue();
                Optional<Item> copayItem = itemService.findFirstByCategory(ItemCategory.CoPay);
                if (copayItem.isPresent()) {
                    Item item = copayItem.get();
                    PatientBill patientbill = new PatientBill();
                    patientbill.setVisit(visit);
                    patientbill.setPatient(visit.getPatient());
                    patientbill.setWalkinFlag(Boolean.FALSE);
                    patientbill.setAmount(copay);
                    patientbill.setDiscount(0D);
                    patientbill.setBalance(copay);
                    patientbill.setBillingDate(LocalDate.now());
                    patientbill.setPaymentMode(visit.getPaymentMethod().name());
                    patientbill.setStatus(BillStatus.Draft);

                    String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
                    String bill_no = sequenceNumberService.next(1L, Sequences.BillNumber.name());

                    patientbill.setBillNumber(bill_no);
                    patientbill.setTransactionId(trdId);

                    PatientBillItem billItem = new PatientBillItem();

                    billItem.setBillingDate(LocalDate.now());
                    billItem.setTransactionId(trdId);
                    billItem.setItem(item);
                    billItem.setPaid(false);
                    billItem.setPrice(copay);
                    billItem.setQuantity(1D);
                    billItem.setAmount(copay);
                    billItem.setDiscount(0D);
                    billItem.setBalance(copay);
                    //determine where the copay should be posted
                    billItem.setServicePoint("Copayment");
                    billItem.setServicePointId(0L);
                    billItem.setStatus(BillStatus.Draft);
                    billItem.setMedicId(null);

                    patientbill.addBillItem(billItem);

                    return save(patientbill);
                }

            }
        }
        return null;
    }
    //TODO 2020-05-03 -> filter all the bills and group them together

    // Get Bills
    public List<SummaryBill> getBillTotals(String visitNumber, String patientNumber, Boolean hasBalance, Boolean isWalkin, VisitEnum.PaymentMethod paymentMode, DateRange range) {
        return billItemRepository.getBillSummary(visitNumber, patientNumber, hasBalance, isWalkin, paymentMode, range);

    }

    public BillDetail getBillDetails(String visitNumber, Pageable pageable) {
        List<PatientBillItem> patientItems = billItemRepository.findAll(withVisitNumber(visitNumber));
        List<PatientBillItem> walkinItems = billItemRepository.findAll(withWalkinNumber(visitNumber));
        if (!walkinItems.isEmpty()) {
            patientItems.addAll(walkinItems);
        }
        BillDetail details = new BillDetail();

        List<BillItem> bills = new ArrayList<>();
        List<BillItem> paidBills = new ArrayList<>();
        List<BillPayment> payments = new ArrayList<>();

        patientItems.stream()
                .forEach(x -> {
                    if (x.getBalance() > 0) {
                        bills.add(x.toBillItem());
                    } else {
                        paidBills.add(x.toBillItem());
                        BillPayment.Type type = x.getItem().getCategory() == ItemCategory.CoPay ? BillPayment.Type.Copayment : BillPayment.Type.Receipt;
                        BigDecimal amount = BigDecimal.valueOf(x.getAmount());
                        if (amount.signum() == -1) {
                            amount = amount.negate();
                        }
                        payments.add(new BillPayment(type, x.getPaymentReference(), amount));
                    }
                });

        details.setBills(bills);
        details.setPaidBills(paidBills);
        details.setPayments(payments);

        return details;

//        Map<BillPayment.Type, Double> paymentlist = allPayments
//                .stream()
//                .collect(
//                        Collectors.groupingBy(BillPayment::getType, Collectors.summingDouble(BillPayment::getAmount))
//                );
//          List<BillPayment> payments = new ArrayList<>();
//        paymentlist
//                .forEach((k, v) -> {
//                     payments.add(new BillPayment(k, visitNumber, v));
//                });
    }

    public List<BillItem> getAllBillDetails(String visitNumber) {
        List<PatientBillItem> patientItems = billItemRepository.findAll(withVisitNumber(visitNumber));
        List<PatientBillItem> walkinItems = billItemRepository.findAll(withWalkinNumber(visitNumber));
        if (!walkinItems.isEmpty()) {
            patientItems.addAll(walkinItems);
        }
        BillDetail details = new BillDetail();

        List<BillItem> bills = new ArrayList<>();

        patientItems.stream()
                .forEach(x -> {
                    bills.add(x.toBillItem());
                });

        details.setBills(bills);
        return bills;
    }
    //when changing billing I should only show those

    private Specification<PatientBillItem> withVisitNumber(String visitNo) {
        return (Root<PatientBillItem> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("patientBill").get("walkinFlag"), Boolean.FALSE));
            if (visitNo != null) {
                predicates.add(cb.equal(root.get("patientBill").get("visit").get("visitNumber"), visitNo));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private Specification<PatientBillItem> withWalkinNumber(String walkIn) {
        return (Root<PatientBillItem> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("patientBill").get("walkinFlag"), Boolean.TRUE));

            if (walkIn != null) {
                predicates.add(cb.equal(root.get("patientBill").get("reference"), walkIn));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

}
