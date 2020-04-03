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
import io.smarthealth.accounting.billing.domain.PatientBillGroup;
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
import io.smarthealth.accounting.doctors.domain.DoctorInvoice;
import io.smarthealth.accounting.doctors.domain.DoctorItem;
import io.smarthealth.accounting.doctors.service.DoctorInvoiceService;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.laboratory.domain.LabRegisterTest;
import io.smarthealth.clinical.laboratory.domain.LabRegisterTestRepository;
import io.smarthealth.clinical.pharmacy.domain.DispensedDrugRepository;
import io.smarthealth.clinical.radiology.domain.PatientScanTestRepository;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.domain.SearchOperation;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.stores.domain.Store;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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
    public PatientBill createPatientBill(BillData data) {

        //check the validity of the patient visit
        Visit visit = findVisitEntityOrThrow(data.getVisitNumber());
//        String billNumber = RandomStringUtils.randomNumeric(6); //sequenceService.nextNumber(SequenceType.BillNumber);
        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());  //sequenceGenerator.generateTransactionNumber();
        String bill_no = sequenceNumberService.next(1L, Sequences.BillNumber.name());

        PatientBill patientbill = new PatientBill();
        patientbill.setVisit(visit);
        patientbill.setPatient(visit.getPatient());
        patientbill.setAmount(data.getAmount());
        patientbill.setDiscount(data.getDiscount());
        patientbill.setBalance(data.getAmount());
        patientbill.setBillNumber(bill_no);
        patientbill.setBillingDate(data.getBillingDate());
        patientbill.setReferenceNo(data.getReferenceNo());
        patientbill.setPaymentMode(data.getPaymentMode());
        patientbill.setTransactionId(trdId);
        patientbill.setStatus(BillStatus.Draft);

        List<PatientBillItem> lineItems = data.getBillItems()
                .stream()
                .map(lineData -> {
                    Item item = itemService.findItemWithNoFoundDetection(lineData.getItemCode());
                    PatientBillItem billItem = new PatientBillItem();

                    billItem.setBillingDate(lineData.getBillingDate());
                    billItem.setTransactionId(trdId);
                    billItem.setItem(item);
                    billItem.setPrice(lineData.getPrice());
                    billItem.setQuantity(lineData.getQuantity());
                    billItem.setAmount(lineData.getAmount());
                    billItem.setDiscount(lineData.getDiscount());
                    billItem.setBalance(lineData.getAmount());
                    billItem.setServicePoint(lineData.getServicePoint());
                    billItem.setServicePointId(lineData.getServicePointId());
                    billItem.setStatus(BillStatus.Draft);
                    billItem.setMedicId(lineData.getMedicId());
//                    if (item != null) {
//                        billItem.setNarration(item.getItemName());
//                    }else{
//                        billItem.setNarration(lineData.getNarration());
//                    }
                    return billItem;
                })
                .collect(Collectors.toList());
        patientbill.addBillItems(lineItems);

        PatientBill savedBill = save(patientbill);

        if (savedBill.getPaymentMode().equals("Insurance")) {
            journalService.save(toJournal(savedBill, null));
        }
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
        PatientBill savedBill = patientBillRepository.saveAndFlush(bill);
        List<DoctorInvoice> doctorInvoices = toDoctorInvoice(savedBill);
        if (doctorInvoices.size() > 0) {
            doctorInvoices.forEach(inv -> doctorInvoiceService.save(inv));
        }
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

    public Page<PatientBill> findAllBills(String transactionNo, String visitNo, String patientNo, String paymentMode, String billNo, BillStatus status, DateRange range, Pageable page) {

        Specification<PatientBill> spec = BillingSpecification.createSpecification(transactionNo, visitNo, patientNo, paymentMode, billNo, status, range);

        return patientBillRepository.findAll(spec, page);

    }

    public Visit findVisitEntityOrThrow(String visitNumber) {
        return this.visitRepository.findByVisitNumber(visitNumber)
                .orElseThrow(() -> APIException.notFound("Visit Number {0} not found.", visitNumber));
    }

    public List<PatientBillGroup> getPatientBillGroups(BillStatus status) {
        return patientBillRepository.groupBy(status);
    }

    public Page<PatientBillItem> getPatientBillItemByVisit(String visitNumber, Pageable page) {
        Visit visit = findVisitEntityOrThrow(visitNumber);

        return billItemRepository.findPatientBillItemByVisit(visit, page);
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
        String debitAcc = debitAccount.get().getAccount().getIdentifier();

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

//                items.add(new JournalEntryItem(desc, debitAcc, JournalEntryItem.Type.DEBIT, amount));
//                items.add(new JournalEntryItem(desc, credit.getIdentifier(), JournalEntryItem.Type.CREDIT, amount));
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

//                        items.add(new JournalEntryItem(desc, debit.getIdentifier(), JournalEntryItem.Type.DEBIT, amount));
//                        items.add(new JournalEntryItem(desc, credit.getIdentifier(), JournalEntryItem.Type.CREDIT, amount));
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
}
