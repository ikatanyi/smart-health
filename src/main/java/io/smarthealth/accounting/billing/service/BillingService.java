package io.smarthealth.accounting.billing.service;

import io.smarthealth.accounting.acc.service.JournalEntryService;
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
import io.smarthealth.clinical.pharmacy.data.PharmacyData;
import io.smarthealth.infrastructure.numbers.service.SequenceNumberGenerator;
import lombok.RequiredArgsConstructor;
import io.smarthealth.accounting.billing.domain.PatientBillRepository;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.stock.stores.domain.Store;
import lombok.extern.slf4j.Slf4j;

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
    private final SequenceNumberGenerator sequenceGenerator;
//    private final TxnService txnService;
    private final JournalEntryService journalService;

    public PatientBill createPatientBill(BillData data) {
        //check the validity of the patient visit
        Visit visit = findVisitEntityOrThrow(data.getVisitNumber());
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

                    if (lineData.getItemCode() != null) {
                        Item item = itemService.findItemWithNoFoundDetection(lineData.getItemCode());
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

        if (savedBill.getPaymentMode().equals("Insurance")) {
            journalService.createJournalEntry(savedBill);  
        }
        return savedBill;
    }

    public void createPharmacyBill(Store store, PharmacyData data) {
        PatientBill bill = createBill(data);
        if (bill.getPaymentMode().equals("Insurance")) {
            journalBill(bill, store);
        }
    }

    private PatientBill createBill(PharmacyData data) {
        ServicePoint srvpoint = servicePointService.getServicePointByType(ServicePointType.Pharmacy);
        log.info("Generating a patient bill from stock item");
        //check the validity of the patient visit
        Visit visit = findVisitEntityOrThrow(data.getVisitNumber());

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

                    billItem.setBillingDate(data.getDispenseDate());
                    billItem.setTransactionId(data.getTransactionId());
                    billItem.setServicePointId(srvpoint.getId());
                    billItem.setServicePoint(srvpoint.getName());

                    if (lineData.getItemCode() != null) {
                        Item item = getItemByCode(lineData.getItemCode());
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
        return save(savedBill);
    }

    private void journalBill(PatientBill bill, Store store) {
        if (bill != null) {
            log.info("journal posting the stock item ... ");
            journalService.createJournalEntry(bill, store);
        }
    }

    public List<PatientBillGroup> getPatientBillGroups(BillStatus status) {
        return patientBillRepository.groupBy(status);
    }

    public Page<PatientBillItem> getPatientBillItemByVisit(String visitNumber, Pageable page) {
        Visit visit = visitRepository.findByVisitNumber(visitNumber).orElseThrow(() -> APIException.notFound("Visit Number {0} not found", visitNumber));

        return billItemRepository.findPatientBillItemByVisit(visit, page);
    }

    public PatientBill save(PatientBill bill) {
        return patientBillRepository.saveAndFlush(bill);
    }
    public PatientBill update(PatientBill bill){
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

    public Page<PatientBill> findAllBills(String transactionNo, String visitNo, String patientNo, String paymentMode, String billNo, BillStatus status, Pageable page) {

        Specification<PatientBill> spec = BillingSpecification.createSpecification(transactionNo, visitNo, patientNo, paymentMode, billNo, status);

        return patientBillRepository.findAll(spec, page);

    }

    public Visit findVisitEntityOrThrow(String visitNumber) {
        return this.visitRepository.findByVisitNumber(visitNumber)
                .orElseThrow(() -> APIException.notFound("Visit Number {0} not found.", visitNumber));
    }

    private String roundedAmount(Double amt) {
        return BigDecimal.valueOf(amt)
                .setScale(2, BigDecimal.ROUND_HALF_EVEN)
                .toString();
    }

}
