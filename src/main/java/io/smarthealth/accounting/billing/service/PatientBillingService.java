package io.smarthealth.accounting.billing.service;

import io.smarthealth.accounting.accounts.data.FinancialActivity;
import io.smarthealth.accounting.accounts.domain.*;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.accounting.billing.data.*;
import io.smarthealth.accounting.billing.domain.*;
import io.smarthealth.accounting.billing.domain.enumeration.BillEntryType;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.doctors.domain.DoctorInvoice;
import io.smarthealth.accounting.doctors.domain.DoctorItem;
import io.smarthealth.accounting.doctors.service.DoctorInvoiceService;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.payment.data.BillReceiptedItem;
import io.smarthealth.accounting.payment.data.ReceivePayment;
import io.smarthealth.accounting.payment.domain.Receipt;
import io.smarthealth.accounting.payment.domain.repository.ReceiptRepository;
import io.smarthealth.accounting.pricelist.service.PricelistService;
import io.smarthealth.administration.config.domain.GlobalConfigNum;
import io.smarthealth.administration.config.domain.GlobalConfiguration;
import io.smarthealth.administration.config.service.ConfigService;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.pharmacy.data.DrugRequest;
import io.smarthealth.clinical.pharmacy.data.ReturnedDrugData;
import io.smarthealth.clinical.pharmacy.domain.DispensedDrug;
import io.smarthealth.clinical.pharmacy.service.DispensingService;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.PaymentDetails;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.clinical.visit.service.PaymentDetailsService;
import io.smarthealth.debtor.scheme.domain.SchemeConfigurations;
import io.smarthealth.debtor.scheme.domain.SchemeConfigurationsRepository;
import io.smarthealth.debtor.scheme.domain.enumeration.CoPayType;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemRepository;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.domain.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.smarthealth.stock.item.domain.enumeration.ItemCategory.CoPay;
import static io.smarthealth.stock.item.domain.enumeration.ItemCategory.NHIF_Rebate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientBillingService {

    private final ServicePointService servicePointService;
    private final PatientBillRepository patientBillRepository;
    private final PatientBillItemRepository billItemRepository;
    private final ItemRepository itemRepository;
    private final VisitRepository visitRepository;
    private final SchemeConfigurationsRepository schemeConfigurationsRepository;
    private final JournalService journalService;
    private final SequenceNumberService sequenceNumberService;
    private final FinancialActivityAccountRepository activityAccountRepository;
    private final DoctorInvoiceService doctorInvoiceService;
    private final CashPaidUpdater cashPaidUpdater;
    private final PaymentDetailsService paymentDetailsService;
    private final PricelistService pricelistService;
    private final ReceiptRepository receiptRepository;
    private final ConfigService configurationService;
    private final StoreRepository storeRepository;
    private final DispensingService dispensingService;

    private static Specification<PatientBillItem> findPatientBillItemsWith(String visitNo, boolean includeCanceled, PaymentMethod paymentMethod, BillEntryType billEntryType) {
        return (root, query, builder) -> {
            ArrayList<Predicate> predicateList = new ArrayList<>();
            predicateList.add(builder.equal(root.get("patientBill").get("walkinFlag"), Boolean.FALSE));
            if (visitNo != null) {
                predicateList.add(builder.equal(root.get("patientBill").get("visit").get("visitNumber"), visitNo));
            }
            if (!includeCanceled) {
                predicateList.add(builder.notEqual(root.get("status"), BillStatus.Canceled));
            }
            if (paymentMethod != null) {
                predicateList.add(builder.equal(root.get("billPayMode"), paymentMethod));
            }

            if (billEntryType != null) {
                predicateList.add(builder.equal(root.get("entryType"), billEntryType));
            }
            return builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        };
    }

    public static <T> T getValueOrDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PatientBill createPatientBill(BillData data) {
        log.debug("Creating a patient bill {} ", data);

        PatientBill toSaveBill = createPatientBill(data.getPatientNumber(), data.getPatientName(), data.getVisitNumber(), data.getBillingDate(), data.getPaymentMode(), data.getAmount(), data.getDiscount(), data.getWalkinFlag());

        List<PatientBillItem> lineItems = data.getBillItems()
                .stream()
                .map(lineData -> {
                    Item item = getItemById(lineData.getItemId());

                    PatientBillItem billItem = new PatientBillItem();
                    billItem.setBillingDate(data.getBillingDate());
                    billItem.setTransactionId(toSaveBill.getTransactionId());
                    billItem.setItem(item);
                    billItem.setPaid(data.getPaymentMode().equals("Insurance"));
                    billItem.setPrice(lineData.getPrice());
                    billItem.setQuantity(lineData.getQuantity());

                    if (billItem.getPrice() != null) {
                        billItem.setAmount((billItem.getPrice() * billItem.getQuantity()));
                    } else {
                        billItem.setAmount(lineData.getAmount());
                    }
                    if (item.getCategory().equals(CoPay)) {
                        billItem.setPrice(data.getAmount());
                    }

                    billItem.setDiscount(lineData.getDiscount());
                    billItem.setBalance((billItem.getAmount()));
                    billItem.setServicePoint(lineData.getServicePoint());
                    billItem.setServicePointId(lineData.getServicePointId());
                    billItem.setStatus(BillStatus.Draft);
                    billItem.setMedicId(lineData.getMedicId());
                    billItem.setBillPayMode(lineData.getPaymentMethod());
                    billItem.setEntryType(lineData.getEntryType());

                    return billItem;
                })
                .collect(Collectors.toList());

        toSaveBill.addBillItems(lineItems);

        System.out.println("End of bill items");

        PatientBill savedBill = save(toSaveBill);

        List<PatientBillItem> inventoryItems = savedBill.getBillItems().stream()
                .filter(i -> i.getItem().getItemType() == ItemType.Inventory)
                .collect(Collectors.toList());

        dispensingService.dispenseConsumables(inventoryItems, MovementType.Dispensed, MovementPurpose.Issue);

        if (savedBill.getPaymentMode().equals("Insurance") || (savedBill.getVisit() != null && savedBill.getVisit().getVisitType() == VisitEnum.VisitType.Inpatient)) {
            journalService.save(toJournal(savedBill, null));
        }
        //TODO stock entry for the consumable marked as consumables from the stores

        return savedBill;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PatientBill createPatientBill(DrugRequest drugRequest) {
        Store store = storeRepository.findById(drugRequest.getStoreId())
                .orElseThrow(() -> APIException.notFound("Store with id {0} not found", drugRequest.getStoreId()));

        PatientBill patientBill = createPatientBill(drugRequest.getPatientNumber(), drugRequest.getPatientName(), drugRequest.getVisitNumber(), drugRequest.getDispenseDate(), drugRequest.getPaymentMode(), drugRequest.getAmount(), drugRequest.getDiscount(), drugRequest.getIsWalkin());
        final ServicePoint srvpoint = store.getServicePoint();

        List<PatientBillItem> lineItems = drugRequest.getDrugItems()
                .stream()
                .map(lineData -> {
                    PatientBillItem billItem = new PatientBillItem();
                    Item item = getItemById(lineData.getItemId());
                    billItem.setBillingDate(drugRequest.getDispenseDate());
                    billItem.setTransactionId(patientBill.getTransactionId());
                    billItem.setServicePointId(srvpoint.getId());
                    billItem.setServicePoint(srvpoint.getName());
                    billItem.setItem(item);
                    billItem.setPrice(lineData.getPrice());
                    billItem.setQuantity(lineData.getQuantity());
                    billItem.setAmount(lineData.getAmount());
                    billItem.setDiscount(lineData.getDiscount());
                    billItem.setBalance(lineData.getAmount());
                    billItem.setPaid(drugRequest.getPaymentMode() != null ? drugRequest.getPaymentMode().equals("Insurance") : false);
                    billItem.setBillPayMode(lineData.getPaymentMethod());
                    billItem.setStatus(BillStatus.Draft);
                    billItem.setEntryType(lineData.getEntryType());
                    return billItem;
                })
                .collect(Collectors.toList());

        patientBill.addBillItems(lineItems);
        //Check the current configuration if pay first then do not dispense just create the bill and allow to be dispensed on receipting
        PatientBill savedBill = patientBillRepository.save(patientBill);

        dispensingService.dispenseItem(drugRequest, store);

        return patientBill;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PatientBill createCopay(CopayData data) {
        String visitNumber = data.getVisitNumber();
        Long schemeId = data.getSchemeId();
        Visit visit = visitRepository.findByVisitNumberAndStatusNot(visitNumber, VisitEnum.Status.CheckOut)
                .orElseThrow(() -> APIException.badRequest("Patient Visit {} is not Active. Patient is Checked out", visitNumber));

        Optional<SchemeConfigurations> schemeConfigs = schemeConfigurationsRepository.findSchemeConfigurationsBySchemeId(schemeId);

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
                if (copay <= 0) {
                    return null;
                }
                Optional<Item> copayItem = itemRepository.findFirstByCategory(CoPay);
                if (copayItem.isPresent()) {
                    PatientBill receiptBill = createPatientBill("", "", visitNumber, LocalDate.now(), "Cash", copay, 0D, false);
                    PatientBillItem billsItem = new PatientBillItem();

                    billsItem.setBillingDate(LocalDate.now());
                    billsItem.setTransactionId(receiptBill.getTransactionId());
                    billsItem.setItem(copayItem.get());
                    billsItem.setPaid(false);
                    billsItem.setPrice(copay);
                    billsItem.setQuantity(1D);
                    billsItem.setAmount(copay);
                    billsItem.setDiscount(0D);
                    billsItem.setBalance(copay);
                    billsItem.setServicePoint("Copayment Fee");
                    billsItem.setServicePointId(0L);
                    billsItem.setStatus(BillStatus.Draft);
                    billsItem.setMedicId(null);
                    billsItem.setBillPayMode(PaymentMethod.Cash);
                    billsItem.setEntryType(BillEntryType.Credit);
                    receiptBill.addBillItem(billsItem);
                    return save(receiptBill);
                }
            }

        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PatientBill createReceiptItem(String patientNumber, String patientName, String visitNumber, Double amount, ItemCategory itemCategory, boolean isWalking,String reference) {
        //I need to pass copay as part of this
        Visit visit = null;
        if (!isWalking) {
//            visit = visitRepository.findByVisitNumberAndStatusNot(visitNumber, VisitEnum.Status.CheckOut)
//                    .orElseThrow(() -> APIException.badRequest("Patient Visit {0} is not Active. Patient is Checked out", visitNumber));
            visit = visitRepository.findByVisitNumber(visitNumber)
                    .orElseThrow(() -> APIException.notFound("Visit ID {0} Not Found", visitNumber));
        }

        Optional<Item> receiptItem = itemRepository.findFirstByCategory(itemCategory);

        Item creditItem = receiptItem.get();
        //define the type of receipting if copay
        String description = creditItem.getItemName() != null ? creditItem.getItemName() : "Receipt";

        if (creditItem != null) {
            PatientBill receiptBill = createPatientBill(patientNumber, patientName, visitNumber, LocalDate.now(), "Cash", amount, 0D, isWalking);
            PatientBillItem billsItem = new PatientBillItem();

            billsItem.setBillingDate(LocalDate.now());
            billsItem.setTransactionId(receiptBill.getTransactionId());
            billsItem.setItem(creditItem);
            billsItem.setPaid(true);
            billsItem.setPrice(amount);
            billsItem.setQuantity(1D);
            billsItem.setAmount(amount);
            billsItem.setDiscount(0D);
            billsItem.setBalance(amount);
            billsItem.setServicePoint(description);
            billsItem.setServicePointId(0L);
            billsItem.setStatus(BillStatus.Paid);
            billsItem.setMedicId(null);
            billsItem.setBillPayMode(itemCategory == NHIF_Rebate ? PaymentMethod.Insurance :PaymentMethod.Cash);
            billsItem.setEntryType(BillEntryType.Credit);
            billsItem.setPaymentReference(reference);
//            billsItem.setTransactionType(TransactionType.Receipting);
            //can add the transaction type


            receiptBill.addBillItem(billsItem);
            return save(receiptBill);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PatientBill createReceiptItem(String visitNumber, Invoice invoice){
        Patient patient  = invoice.getPatient();

        Visit visit = visitRepository.findByVisitNumber(visitNumber)
                    .orElseThrow(() -> APIException.notFound("Visit ID {0} Not Found", visitNumber));


        Optional<Item> receiptItem = itemRepository.findFirstByCategory(ItemCategory.Receipt);

        Item creditItem = receiptItem.get();
        //define the type of receipting if copay
        String description = "Invoice";
        Double amount = invoice.getAmount().doubleValue();

        if (creditItem != null) {
            PatientBill receiptBill = createPatientBill(patient.getPatientNumber(), patient.getFullName(), visitNumber, LocalDate.now(), "Cash", amount, 0D, false);
            PatientBillItem billsItem = new PatientBillItem();
            billsItem.setBillingDate(LocalDate.now());
            billsItem.setTransactionId(receiptBill.getTransactionId());
            billsItem.setItem(creditItem);
            billsItem.setPaid(true);
            billsItem.setPrice(amount);
            billsItem.setQuantity(1D);
            billsItem.setAmount(amount);
            billsItem.setDiscount(0D);
            billsItem.setBalance(amount);
            billsItem.setServicePoint(description);
            billsItem.setServicePointId(0L);
            billsItem.setStatus(BillStatus.Paid);
            billsItem.setMedicId(null);
            billsItem.setBillPayMode(PaymentMethod.Insurance);
            billsItem.setEntryType(BillEntryType.Credit);
            billsItem.setPaymentReference(invoice.getNumber());
            billsItem.setInvoiceNumber(invoice.getNumber());
//            billsItem.setTransactionType(TransactionType.Receipting);
            //can add the transaction type
            //instead of creating new invoice

            receiptBill.addBillItem(billsItem);
            return save(receiptBill);
        }
        return null;
        //Patient patient = invoice.getPatient();
        //return createReceiptItem(patient.getPatientNumber(), patient.getFullName(), visitNumber, invoice.getAmount().doubleValue(),ItemCategory.Receipt, false,invoice.getNumber());
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<DispensedDrug> drugReturns(String visitNumber, List<ReturnedDrugData> returnedDrugs) {
        Visit visit = findVisitEntityOrThrow(visitNumber);
        //TODO void the items

        return dispensingService.returnItems(visitNumber, returnedDrugs);
    }

    public PatientBill save(PatientBill bill) {
        log.debug("Persisting bill to database {} ", bill);
        checkPatientBillLimit(bill);

        PatientBill billSaved = patientBillRepository.save(bill);

        if (bill.getWalkinFlag() == null || bill.getWalkinFlag()) {
            return billSaved;
        }

        List<DoctorInvoice> doctorInvoices = toDoctorInvoice(billSaved);
        if (doctorInvoices.size() > 0) {
            doctorInvoices.forEach(inv -> doctorInvoiceService.save(inv));
        }

        return billSaved;
    }

//    public List<PatientBillDetail> getPatientBills(String search, String patientNumber, String visitNumber, PaymentMethod paymentMethod, Long payerId, Long schemeId, VisitEnum.VisitType visitType, DateRange range, Pageable pageable) {
//        List<PatientBillDetail> patientBills = patientBillRepository.getPatientBills(search, patientNumber, visitNumber, paymentMethod, payerId, schemeId, visitType, range);
//        return patientBills;
//    }

    public Page<VisitBillSummary> getVisitBills(BillingQuery query){
       return patientBillRepository.getVisitBill(query);
    }

    //TODO also find the walking bills and add them
    public Page<PatientBillItem> getPatientBillItems(String visitNumber, boolean includeCanceled, PaymentMethod paymentMethod, BillEntryType billEntryType, Pageable pageable) {
        return billItemRepository.findAll(findPatientBillItemsWith(visitNumber, includeCanceled, paymentMethod, billEntryType), pageable);
    }
    public List<PatientBillItem> getInterimBillItems(String visitNumber){
        return billItemRepository.getByVisitNumberStatus(visitNumber);
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<PatientBillItem> voidPatientBillItem(String visitNumber, List<VoidBillItem> items) {

        List<PatientBillItem> inventoryItems = new ArrayList<>();

        List<PatientBillItem> toVoidList = items
                .stream()
                .map(x -> billItemRepository.findById(x.getBillItemId()).orElse(null))
                .filter(bill -> bill != null && bill.getEntryType() == BillEntryType.Debit)
                .map(patientBill -> {
                    patientBill.setStatus(BillStatus.Canceled);
                    patientBill.setBalance(0D);
                    patientBill.setPaid(Boolean.FALSE);
                    if (patientBill.getItem().getItemType() == ItemType.Inventory) {
                        inventoryItems.add(patientBill);
                    }
                    return patientBill;
                })
                .collect(Collectors.toList());

        List<PatientBillItem> bills = billItemRepository.saveAll(toVoidList);

        if (!bills.isEmpty()) {
            PatientBill pb = bills.get(0).getPatientBill();

            if (pb.getPaymentMode().equals("Insurance")) {
                journalService.save(toReverseJournal(bills, null));
            }
        }
        dispensingService.dispenseConsumables(inventoryItems, MovementType.Returns, MovementPurpose.Returns);
        return bills;
    }

    public String finalizeBill(String visitNumber, BillFinalizeData finalizeBill) {
        Visit visit = visitRepository.findByVisitNumberAndStatusNot(visitNumber, VisitEnum.Status.CheckOut)
                .orElseThrow(() -> APIException.badRequest("Visit Number {0} is not active for transaction", visitNumber));

        String cinvoice = sequenceNumberService.next(1L, Sequences.CashBillNumber.name());
        List<PatientBillItem> lists = finalizeBill.getBillItems()
                .stream()
                .map(x -> {
                    PatientBillItem item = findBillItemById(x.getBillItemId());
                    if (item.isFinalized() == false) {
                        item.setPaid(Boolean.TRUE);
                        item.setStatus(BillStatus.Paid);
                        if (item.getPaymentReference() == null) {
                            item.setPaymentReference(cinvoice);
                        }
                        item.setFinalized(true);
                        item.setInvoiceNumber(cinvoice);
                        item.setBalance(0D);
                        return item;
                    }
                    return null;
                })
                .filter(x -> x != null)
                .collect(Collectors.toList());

        //TODO consider expensing the deposits received
        List<PatientBillItem> updatedBills = billItemRepository.saveAll(lists);

        updatedBills
                .forEach(pi -> {
                            if (pi.getItem().getCategory() == ItemCategory.Receipt) {
                                Optional<Receipt> savedReceipt = receiptRepository.findByReceiptNo(pi.getPaymentReference());
                                if (savedReceipt.isPresent() && savedReceipt.get().getPrepayment()) {
                                    // we have a deposit to journal it

                                }
                            }
                        }
                );

        return cinvoice;
    }
   // create an invoice entry

    @Transactional
    public List<PatientBillItem> allocateBillPayment(ReceivePayment data) {

        List<PatientBillItem> receiptingBills = new ArrayList<>();
        List<PatientBillItem> toCreateBillAndReceipt = new ArrayList<>();

        if (!data.getBillItems().isEmpty()) {
            data.getBillItems().stream()
                    .forEach(x -> {
                        if (x.getBillItemId() != null) {
                            PatientBillItem item = findBillItemById(x.getBillItemId());
                            BigDecimal discount = (x.getDiscount() != null ? x.getDiscount() : BigDecimal.ZERO);
                            BigDecimal totalAmount = BigDecimal.valueOf((item.getQuantity() * item.getPrice()));
                            BigDecimal netAmount = totalAmount.subtract(discount);
                            BigDecimal balance = netAmount.subtract(x.getAmount());
                            item.setPaid(Boolean.TRUE);
                            item.setStatus(BillStatus.Paid);
                            if (item.getItem().getCategory() == CoPay) {
//                                item.setAmount((item.getAmount() * -1));
                            } else {
                                item.setAmount(totalAmount.doubleValue());
                            }

                            item.setPaymentReference(data.getReceiptNo());
                            item.setBalance(balance.doubleValue());
                            item.setDiscount(discount.doubleValue());
                            PatientBillItem i = updateBillItem(item);

                            receiptingBills.add(i);

                        } else {
                            PatientBillItem item = createBillItem(x);
//                            if (item.getItem().getCategory() == CoPay) {
//                                item.setAmount((item.getAmount() * -1));
//                            }
                            item.setMedicId(x.getMedicId());
                            item.setTransactionId(data.getTransactionNo());
                            item.setPaymentReference(data.getReceiptNo());
                            toCreateBillAndReceipt.add(item);
                        }
                    });
        }
        if (!toCreateBillAndReceipt.isEmpty()) {
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
            Double amount = toCreateBillAndReceipt.stream()
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
            patientbill.addBillItems(toCreateBillAndReceipt);

            PatientBill savedBill = save(patientbill);
            receiptingBills.addAll(savedBill.getBillItems());
        }

        // add this receipt to the patient bill
        String patientNumber, patientName = null;
        boolean isWalking = false;
        if (data.getWalkin() != null && !data.getWalkin()) {
            Visit visit = findVisitEntityOrThrow(data.getVisitNumber());
            patientNumber = visit.getPatient().getPatientNumber();
            patientName = visit.getPatient().getFullName();
        } else {
            patientNumber = data.getPayerNumber();
            patientName = data.getPayer();
            isWalking = true;
        }
        BigDecimal receiptAmount = data.getBillItems().stream()
                .map(BillReceiptedItem::getAmount)
                .reduce(BigDecimal.ZERO, (x, y) -> x.add(y));
        System.err.println("Comparing Incoming: "+data.getAmount()+" : Calculated. "+receiptAmount);
//        if this is copay no need to create the receipt
        createReceiptItem(patientNumber, patientName, data.getVisitNumber(), receiptAmount.doubleValue(), ItemCategory.Receipt, isWalking, data.getReceiptNo());

        return receiptingBills;
    }

    public PatientBillItem updateBillItem(PatientBillItem item) {
        //determine the request origin and update ti
        cashPaidUpdater.updateRequestStatus(item);
        //TODO update the doctors payments with this receipts
        return billItemRepository.save(item);
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
                    .filter(p -> p.getEntryType() == BillEntryType.Debit)
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

    private JournalEntry toReverseJournal(List<PatientBillItem> bills, Store store) {

        log.debug("Reversing the journal for the patient bills");

        String trdId = String.format("JR%s", sequenceNumberService.next(1L, Sequences.Transactions.name()));
        String description = "Patient Billing Journal Reversal";
        Optional<FinancialActivityAccount> reverseCredit = activityAccountRepository.findByFinancialActivity(FinancialActivity.Patient_Control);
        if (!reverseCredit.isPresent()) {
            throw APIException.badRequest("Patient Control Account is Not Mapped");
        }
//        String debitAcc = debitAccount.get().getAccount().getIdentifier();
        List<JournalEntryItem> items = new ArrayList<>();

        if (!bills.isEmpty()) {
            Map<Long, Double> map = bills
                    .stream()
                    .collect(Collectors.groupingBy(PatientBillItem::getServicePointId,
                            Collectors.summingDouble(PatientBillItem::getAmount)
                            )
                    );
            //then here since we making a revenue
            map.forEach((k, v) -> {
                ServicePoint srv = servicePointService.getServicePoint(k);
                String desc = srv.getName() + " Patient Billing Reversal";
                Account reverseDebit = srv.getIncomeAccount();
                BigDecimal amount = BigDecimal.valueOf(v);

                items.add(new JournalEntryItem(reverseDebit, desc, amount, BigDecimal.ZERO));
                items.add(new JournalEntryItem(reverseCredit.get().getAccount(), desc, BigDecimal.ZERO, amount));

            });
            //if inventory expenses this shit!
            if (store != null) {
                Map<Long, Double> inventory = bills
                        .stream()
                        .filter(x -> x.getItem().isInventoryItem())
                        .collect(Collectors.groupingBy(PatientBillItem::getServicePointId,
                                Collectors.summingDouble(x -> (x.getItem().getCostRate().doubleValue() * x.getQuantity()))
                                )
                        );
                if (!inventory.isEmpty()) {
                    inventory.forEach((k, v) -> {

                        String desc = "Stock Returns ";
                        ServicePoint srv = servicePointService.getServicePoint(k);
                        Account creditExpense = srv.getExpenseAccount(); // cost of sales
                        Account debitInventory = srv.getInventoryAssetAccount();//store.getInventoryAccount(); // Inventory Asset Account
                        BigDecimal amount = BigDecimal.valueOf(v);

                        items.add(new JournalEntryItem(debitInventory, desc, amount, BigDecimal.ZERO));
                        items.add(new JournalEntryItem(creditExpense, desc, BigDecimal.ZERO, amount));
                        //this should return the stocks
                    });
                }
            }
        }

        JournalEntry toSave = new JournalEntry(LocalDate.now(), description, items);
        toSave.setTransactionNo(trdId);
        toSave.setTransactionType(TransactionType.Bill_Reversal);
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }

    private List<DoctorInvoice> toDoctorInvoice(PatientBill billItems) {
        return billItems.getBillItems()
                .stream()
                .filter(p -> p.getEntryType() == BillEntryType.Debit)
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

    private void checkPatientBillLimit(PatientBill bill) {
        log.debug("Start validating patient limit.");
        Optional<GlobalConfiguration> config = configurationService.findByName(GlobalConfigNum.CheckPatientBillLimit.name());
        if (!config.isPresent()) {
            return;
        }
        boolean checkLimit = Boolean.valueOf(config.get().getValue());

        if (!checkLimit) {
            return;
        }

        Optional<PaymentDetails> currentPaymentDetails = null;

        if (bill.getVisit() != null) {
            currentPaymentDetails = paymentDetailsService.fetchPaymentDetailsByVisitWithoutNotFoundDetection(bill.getVisit());
        }
        double amountToBill = 0.00;
        int itemCount = 0;
        //TODO:: refactor scheme limit checks into a method
        //update the scheme that paid this bill if insurance
        for (PatientBillItem b : bill.getBillItems()) {
            //ignore for now the receipts and copay from limit checks
            if (b.getItem().getCategory() != CoPay || b.getItem().getCategory() != ItemCategory.Receipt) {
                amountToBill = amountToBill + b.getAmount();
                itemCount++;
            }
            if (bill.getVisit() != null) {
                //Billing paymode added to accomodate exclusions and other functionalities
//                b.setBillPayMode(bill.getVisit().getPaymentMethod());
                if (bill.getVisit().getPaymentMethod().equals(PaymentMethod.Insurance) && currentPaymentDetails.isPresent()) {
                    b.setScheme(currentPaymentDetails.get().getScheme());
                }
            }
        }

        log.debug("START validate limit amount");

        if (bill.getVisit() != null) {
            currentPaymentDetails = paymentDetailsService.fetchPaymentDetailsByVisitWithoutNotFoundDetection(bill.getVisit());
            PaymentDetails payDetails = currentPaymentDetails.orElse(null);
            if (payDetails != null) {
                System.out.println("Lie 235");
                Optional<SchemeConfigurations> schemeConfigurations = payDetails.getScheme() != null ? schemeConfigurationsRepository.findByScheme(payDetails.getScheme()) : Optional.empty();

                if (schemeConfigurations.isPresent() && schemeConfigurations.get().isLimitEnabled()) {
                    if (payDetails.getRunningLimit() < amountToBill && !payDetails.getExcessAmountEnabled() && bill.getVisit().getVisitType().equals(VisitEnum.VisitType.Outpatient)) {
                        throw APIException.badRequest("Bill amount (" + amountToBill + ") exceed running limit amount (" + payDetails.getRunningLimit() + ") ", "");
                    }
                    if (payDetails.getRunningLimit() < amountToBill && payDetails.getExcessAmountEnabled()) {
                        //check if
                        if (payDetails.getLimitReached()) {
                            //proceed to accept excess entry
                            //TODO: register to keep log of the excess amounts with correct pricebook
                            for (PatientBillItem b : bill.getBillItems()) {
                                BigDecimal defaultPrice = b.getItem().getRate(); //default cash selling price
                                if (payDetails.getExcessAmountPayMode() == PaymentMethod.Cash) {
                                    b.setAmount(NumberUtils.createDouble(defaultPrice.toString()));
                                    b.setBillPayMode(PaymentMethod.Cash);
                                } else {
                                    try {
                                        pricelistService.fetchPriceAmountByItemAndPriceBook(b.getItem(), payDetails.getExcessAmountPayer().getPriceBook());
                                        b.setBillPayMode(PaymentMethod.Insurance);
                                        b.setScheme(payDetails.getExcessAmountScheme());
                                    } catch (Exception e) {
                                        b.setAmount(NumberUtils.createDouble(defaultPrice.toString()));
                                    }
                                }
                            }
                        }
                        if (!payDetails.getLimitReached() && itemCount > 0) {
                            throw APIException.badRequest("Bill amount (" + amountToBill + ") exceed \nrunning limit amount (" + payDetails.getRunningLimit() + "). \nRemove one or more items from the bill count", "");
                        }
                    }
//                    }
                }
            }
        }

        //reduce limit amount
        if (currentPaymentDetails != null && currentPaymentDetails.isPresent()) {
            PaymentDetails pdd = currentPaymentDetails.get();
            double newRunningLimit = (pdd.getRunningLimit() - amountToBill);
            pdd.setRunningLimit(newRunningLimit);

            if (newRunningLimit <= amountToBill) {
                pdd.setLimitReached(Boolean.TRUE);
            }
            paymentDetailsService.createPaymentDetails(pdd);
        }
        log.debug("END validate limit amount");
    }

    private PatientBill createPatientBill(String patientNumber, String patientName, String visitNumber, LocalDate billingDate, String paymentMethod, Double amount, Double discount, boolean isWalking) {
        PatientBill patientbill = new PatientBill();
        Visit visit = null;
        if (!isWalking) {
            visit = findVisitEntityOrThrow(visitNumber);
            patientbill.setVisit(visit);
            patientbill.setPatient(visit.getPatient());
            patientbill.setWalkinFlag(Boolean.FALSE);
        } else {
            patientbill.setReference(patientNumber);
            patientbill.setOtherDetails(patientName);
            patientbill.setWalkinFlag(Boolean.TRUE);
        }

        patientbill.setAmount(amount);
        patientbill.setDiscount(discount);
        patientbill.setBalance(amount);
        patientbill.setBillingDate(billingDate);
        patientbill.setPaymentMode(paymentMethod);
        patientbill.setStatus(BillStatus.Draft);
        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String bill_no = sequenceNumberService.next(1L, Sequences.BillNumber.name());
        patientbill.setBillNumber(bill_no);
        patientbill.setTransactionId(trdId);
        return patientbill;
    }

    private PatientBillItem createBillItem(BillReceiptedItem billReceiptedItem) {
        Item item = getItemById(billReceiptedItem.getPricelistItemId());

        PatientBillItem savedItem = new PatientBillItem();
        savedItem.setPrice(billReceiptedItem.getPrice());
        savedItem.setQuantity(billReceiptedItem.getQuantity());
        savedItem.setAmount(billReceiptedItem.getAmount().doubleValue());
        savedItem.setBalance(billReceiptedItem.getAmount().doubleValue());
        savedItem.setBillingDate(LocalDate.now());
        savedItem.setDiscount(0D);
        savedItem.setTaxes(0D);
        savedItem.setItem(item);
        savedItem.setPaid(Boolean.TRUE);
        savedItem.setStatus(BillStatus.Paid);
        savedItem.setBalance(0D);
        savedItem.setServicePoint(billReceiptedItem.getServicePoint());
        savedItem.setServicePointId(billReceiptedItem.getServicePointId());
        savedItem.setBillPayMode(PaymentMethod.Cash);
        savedItem.setEntryType(BillEntryType.Debit);

        return savedItem;
    }

    private BigDecimal computeDoctorFee(DoctorItem item) {
        if (item.getIsPercentage()) {
            BigDecimal doctorRate = item.getAmount().divide(BigDecimal.valueOf(100)).multiply(item.getServiceType().getRate());
            return doctorRate;
        }
        return item.getAmount();
    }

    private Visit findVisitEntityOrThrow(String visitNumber) {
        return this.visitRepository.findByVisitNumber(visitNumber)
                .orElseThrow(() -> APIException.notFound("Visit Number {0} not found.", visitNumber));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> APIException.notFound("Item with ID {0} not found! ", itemId));
    }

    private PatientBillItem findBillItemById(Long id) {
        return billItemRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Bill Item with Id {0} not found", id));
    }

}
