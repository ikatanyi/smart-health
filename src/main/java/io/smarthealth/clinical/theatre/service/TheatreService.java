/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.theatre.service;

import io.smarthealth.accounting.accounts.data.FinancialActivity;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccount;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccountRepository;
import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.doctors.domain.DoctorInvoice;
import io.smarthealth.accounting.doctors.service.DoctorInvoiceService;
import io.smarthealth.clinical.theatre.data.TheatreBill;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.service.ItemService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.clinical.theatre.domain.TheatreFee;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.inventory.service.InventoryService;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.transaction.annotation.Propagation;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class TheatreService {

    private final BillingService billingService;
    private final VisitRepository visitRepository;
    private final SequenceNumberService sequenceNumberService;
    private final ItemService itemService;
    private final DoctorInvoiceService doctorInvoiceService;
    private final TheatreFeeService theatreFeeService;
    private final StoreService storeService;
    private final InventoryService inventoryService;
    private final FinancialActivityAccountRepository activityAccountRepository;
    private final ServicePointService servicePointService;
    private final JournalService journalService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PatientBill createBill(TheatreBill data) {
        PatientBill patientbill = new PatientBill();
        Visit visit = findVisitEntityOrThrow(data.getVisitNumber());
        patientbill.setVisit(visit);
        patientbill.setPatient(visit.getPatient());
        patientbill.setWalkinFlag(Boolean.FALSE);
        patientbill.setAmount(data.getAmount());
        patientbill.setDiscount(0D);
        patientbill.setBalance(data.getAmount());
        patientbill.setBillingDate(data.getBillingDate());
        patientbill.setPaymentMode(data.getPaymentMode()!=null ? data.getPaymentMode() : "Insurance");
        patientbill.setStatus(BillStatus.Draft);

        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String bill_no = sequenceNumberService.next(1L, Sequences.BillNumber.name());

        patientbill.setBillNumber(bill_no);
        patientbill.setTransactionId(trdId);

        List<PatientBillItem> lineItems = data.getItems()
                .stream()
                .map(lineData -> {
                    Item item = itemService.findItemWithNoFoundDetection(lineData.getItemCode());
                    PatientBillItem billItem = new PatientBillItem();

                    billItem.setBillingDate(data.getBillingDate());
                    billItem.setTransactionId(trdId);
                    billItem.setItem(item);
//                    billItem.setPaid(data.getPaymentMode().equals("Insurance"));
                    billItem.setPaid(Boolean.TRUE);
                    billItem.setPrice(lineData.getPrice());
                    if (item.getCategory().equals(ItemCategory.CoPay)) {
                        billItem.setPrice(data.getAmount());
                    }
                    billItem.setQuantity(lineData.getQuantity());
                    if (billItem.getPrice() != null) {
                        billItem.setAmount((billItem.getPrice() * billItem.getQuantity()));
                    } else {
                        billItem.setAmount(lineData.getAmount());
                    }
                    billItem.setDiscount(0D);
                    billItem.setBalance((billItem.getAmount()));

                    billItem.setServicePoint(data.getServicePoint());
                    billItem.setServicePointId(data.getServicePointId());
                    billItem.setStatus(BillStatus.Draft);
                    billItem.setMedicId(null);

                    //determine
                    billItem.setTheatreProviders(lineData.getProviders());
                    //get store for the item if inventory
                    billItem.setStoreId(lineData.getStoreId());

                    return billItem;
                })
                .collect(Collectors.toList());
        patientbill.addBillItems(lineItems);
        //create the bill and post as required
        PatientBill savedBill = billingService.createPatientBill(patientbill);
        //then we bill doctors fee
        List<DoctorInvoice> doctorInvoices = toDoctorInvoice(savedBill);
        if (doctorInvoices.size() > 0) { 
            doctorInvoices.forEach(inv -> doctorInvoiceService.save(inv));
        }
        List<StockEntry> stockEntries = createStockEntries(savedBill);
        if (stockEntries.size() > 0) { 
            inventoryService.saveAll(stockEntries);
            journalService.save(toJournal(savedBill));
        }
        return savedBill;
    }

    private Visit findVisitEntityOrThrow(String visitNumber) {
        return this.visitRepository.findByVisitNumber(visitNumber)
                .orElseThrow(() -> APIException.notFound("Visit Number {0} not found.", visitNumber));
    }

    private BigDecimal computeTheatreFee(TheatreFee item, Double procedureFee) {
        if (item.getIsPercentage()) {
            BigDecimal fee = BigDecimal.valueOf(procedureFee);
            BigDecimal doctorRate = item.getAmount().divide(BigDecimal.valueOf(100)).multiply(fee);
            return doctorRate;
        }
        return item.getAmount();
    }

    private List<DoctorInvoice> toDoctorInvoice(PatientBill bill) {
        List<DoctorInvoice> toInvoice = new ArrayList<>();
        bill.getBillItems()
                .stream()
                .filter(x -> x.getTheatreProviders() != null || !x.getTheatreProviders().isEmpty())
                .forEach(billItem -> {

                    billItem.getTheatreProviders().stream()
                            .forEach(provider -> {
                                Optional<TheatreFee> theatreFee = theatreFeeService.findByItemAndCategory(billItem.getItem(), provider.getRole());
                                if (theatreFee.isPresent()) {
                                    BigDecimal amt = computeTheatreFee(theatreFee.get(), (billItem.getQuantity() * billItem.getPrice()));
                                    Employee doctor = doctorInvoiceService.getDoctorById(provider.getMedicId());
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
                                    invoice.setServiceItem(theatreFee.get());
                                    invoice.setTransactionId(billItem.getTransactionId());
                                    invoice.setVisit(billItem.getPatientBill().getVisit());
                                    toInvoice.add(invoice);
                                }
                            });
                });
        return toInvoice;
    }

    private List<StockEntry> createStockEntries(PatientBill patientBill) {
        return patientBill.getBillItems().stream()
                .filter(x -> x.getStoreId() != null)
                .map(drug -> {
                    Item item = drug.getItem();
                    Store store = storeService.getStoreWithNoFoundDetection(drug.getStoreId());

                    BigDecimal amt = BigDecimal.valueOf(drug.getAmount());
                    BigDecimal price = BigDecimal.valueOf(drug.getPrice());

                    StockEntry stock = new StockEntry();
                    stock.setAmount(amt);
                    stock.setQuantity(drug.getQuantity() * -1);
                    stock.setItem(item);
                    stock.setMoveType(MovementType.Dispensed);
                    stock.setPrice(price);
                    stock.setPurpose(MovementPurpose.Issue);
                    stock.setReferenceNumber(patientBill.getPatient().getPatientNumber());
                    stock.setIssuedTo(patientBill.getPatient().getPatientNumber() + " " + patientBill.getPatient().getFullName());
                    stock.setStore(store);
                    stock.setTransactionDate(drug.getBillingDate());
                    stock.setTransactionNumber(drug.getTransactionId());
                    stock.setUnit("");
                    stock.setBatchNo("-");

                    return stock;
                })
                .collect(Collectors.toList());

    }

    private JournalEntry toJournal(PatientBill bill) {
        String description = "Patient Billing";
        if (bill.getPatient() != null) {
            description = bill.getPatient().getPatientNumber() + " " + bill.getPatient().getFullName();
        }

        Optional<FinancialActivityAccount> debitAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Patient_Control);
        if (!debitAccount.isPresent()) {
            throw APIException.badRequest("Patient Control Account is Not Mapped");
        }
//        String debitAcc = debitAccount.get().getAccount().getIdentifier();
        List<JournalEntryItem> items = new ArrayList<>();

        if (!bill.getBillItems().isEmpty()) {
            Map<Long, Double> inventory = bill.getBillItems()
                    .stream()
                    .filter(x -> x.getItem().isInventoryItem() && x.getStoreId() != null)
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

        JournalEntry toSave = new JournalEntry(bill.getBillingDate(), description, items);
        toSave.setTransactionNo(bill.getTransactionId());
        toSave.setTransactionType(TransactionType.Billing);
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }

}
