package io.smarthealth.clinical.pharmacy.service;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.clinical.pharmacy.data.PharmacyData;
import io.smarthealth.clinical.pharmacy.domain.DispensedDrug;
import io.smarthealth.security.util.SecurityUtils;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.inventory.service.InventoryService;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.smarthealth.clinical.pharmacy.domain.DispensedDrugRepository;
import io.smarthealth.clinical.pharmacy.domain.specification.DispensingSpecification;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.sequence.numbers.service.SequenceNumberGenerator;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.ItemRepository;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class DispensingService {

    private final DispensedDrugRepository repository;
    private final ItemRepository itemRepository;
    private final PatientService patientService;
    private final StoreService storeService;
    private final BillingService billingService;
    private final InventoryService inventoryService;
    private final SequenceNumberService sequenceNumberService;

    private void dispenseItem(Store store, PharmacyData patientDrugs) {
        Patient patient = patientService.findPatientOrThrow(patientDrugs.getPatientNumber());
//        Store store = storeService.getStoreWithNoFoundDetection(patientDrugs.getStoreId());
        if (!patientDrugs.getDrugItems().isEmpty()) {
            patientDrugs.getDrugItems()
                    .stream()
                    .forEach(drugData -> {
                        DispensedDrug drugs = new DispensedDrug();
                        Item item = billingService.getItemByCode(drugData.getItemCode());
                        drugs.setPatient(patient);
                        drugs.setDrug(item);
                        drugs.setStore(store);

                        drugs.setDispensedDate(patientDrugs.getDispenseDate());
                        drugs.setTransactionId(patientDrugs.getTransactionId());
                        drugs.setQtyIssued(drugData.getQuantity());
                        drugs.setPrice(drugData.getPrice());
                        drugs.setAmount(drugData.getAmount());
                        drugs.setUnits(drugData.getUnit());
                        drugs.setDoctorName(drugData.getDoctorName());
                        drugs.setPaid(false);
                        drugs.setCollected(true);
                        drugs.setDispensedBy(SecurityUtils.getCurrentUserLogin().orElse(""));
                        drugs.setCollectedBy("");

                        drugs.setInstructions(drugData.getInstructions());

                        DispensedDrug savedDrug = repository.saveAndFlush(drugs);
                        doStockEntries(savedDrug.getId());
                    });
        }
    }

    @Transactional
    public String dispense(PharmacyData patientDrugs) {
        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());

        Store store = storeService.getStoreWithNoFoundDetection(patientDrugs.getStoreId());
        patientDrugs.setTransactionId(trdId);

        dispenseItem(store, patientDrugs);

        billingService.save(toBill(patientDrugs, store));

        return trdId;
    }

    private void doStockEntries(Long drugId) {
        Optional<DispensedDrug> drug = repository.findById(drugId);
        if (drug.isPresent()) {
            inventoryService.save(StockEntry.create(drug.get()));
        } else {
            System.err.println("Drug entry is empty");
        }
    }

    public DispensedDrug findDispensedDrugOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> APIException.notFound("Dispensed Drug with id {0} not found", id));
    }

    public Page<DispensedDrug> findDispensedDrugs(String transactionNo, String visitNo, String patientNo, String prescriptionNo, String billNo, String status, Pageable page) {
        BillStatus state = BillStatus.valueOf(status);
        Specification<DispensedDrug> spec = DispensingSpecification.createSpecification(transactionNo, visitNo, patientNo, prescriptionNo, billNo, state);

        return repository.findAll(spec, page);

    }

    private PatientBill toBill(PharmacyData data, Store store) {
        //get the service point from store
        Visit visit = billingService.findVisitEntityOrThrow(data.getVisitNumber());
        ServicePoint srvpoint = store.getServicePoint();

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
                    System.err.println("Testing ... "+lineData.getItemCode()+" ID "+lineData.getItemId());
                    System.err.println("Service point ");
                    PatientBillItem billItem = new PatientBillItem();
                    Item item = getItemByCode(lineData.getItemCode());
                    
                    billItem.setBillingDate(data.getDispenseDate());
                    billItem.setTransactionId(data.getTransactionId());
                    billItem.setServicePointId(srvpoint.getId());
                    billItem.setServicePoint(srvpoint.getName());
                    
                    billItem.setItem(item);
//                    if (lineData.getItemId() != null) {
//                        Item item = getItemByCode(lineData.getItemCode());
//                        billItem.setItem(item);
//                    }

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
        return patientbill;
    }

    private Item getItemByCode(String code) {
        return itemRepository.findByItemCode(code)
                .orElseThrow(() -> APIException.notFound("Item with code {0} not found.", code));
    }

    private Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Item with code {0} not found.", id));
    }
}
