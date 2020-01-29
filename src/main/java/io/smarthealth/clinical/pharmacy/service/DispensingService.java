package io.smarthealth.clinical.pharmacy.service;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.clinical.pharmacy.data.PharmacyData;
import io.smarthealth.clinical.pharmacy.domain.DispensedDrug;
import io.smarthealth.infrastructure.common.SecurityUtils;
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
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.numbers.service.SequenceNumberGenerator;
import java.util.Optional;
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
    private final PatientService patientService;
    private final StoreService storeService;
    private final BillingService billingService;
    private final InventoryService inventoryService;
    private final SequenceNumberGenerator sequenceGenerator;

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
        String trdId = sequenceGenerator.generateTransactionNumber();
        Store store = storeService.getStoreWithNoFoundDetection(patientDrugs.getStoreId());
        patientDrugs.setTransactionId(trdId);

        dispenseItem(store, patientDrugs);

        billingService.createPharmacyBill(store, patientDrugs);

        return trdId;
    }

    private String doDispenseDrug(PharmacyData patientDrugs) {;
        String trdId = sequenceGenerator.generateTransactionNumber();

        Patient patient = patientService.findPatientOrThrow(patientDrugs.getPatientNumber());
        Store store = storeService.getStoreWithNoFoundDetection(patientDrugs.getStoreId());

        List<DispensedDrug> dispensedlist = new ArrayList<>();

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
                        drugs.setTransactionId(trdId);
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
                        dispensedlist.add(drugs);

                        DispensedDrug savedDrug = repository.saveAndFlush(drugs);
                        doStockEntries(savedDrug.getId());
                    });
        }
        //billing
        // billingService.doBilling(store, patientDrugs);
//        PatientBill savedBill = billingService.createBill(store, patientDrugs);
        //aftect journals
        return trdId; //savedBill.getTransactionId();
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
}
