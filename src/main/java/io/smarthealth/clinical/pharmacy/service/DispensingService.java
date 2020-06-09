package io.smarthealth.clinical.pharmacy.service;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.clinical.pharmacy.data.DrugRequest;
import io.smarthealth.clinical.pharmacy.data.ReturnedDrugData;
import io.smarthealth.clinical.pharmacy.domain.DispensedDrug;
import io.smarthealth.security.util.SecurityUtils;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.inventory.service.InventoryService;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.smarthealth.clinical.pharmacy.domain.DispensedDrugRepository;
import io.smarthealth.clinical.pharmacy.domain.specification.DispensingSpecification;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.domain.DoctorsRequestRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.notifications.service.RequestEventPublisher;
import io.smarthealth.organization.person.domain.WalkIn;
import io.smarthealth.organization.person.service.WalkingService;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.ItemRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
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
    private final VisitService visitService;
    private final DoctorsRequestRepository doctorRequestRepository;
    private final WalkingService walkingService;
    private final RequestEventPublisher requestEventPublisher;

    private void dispenseItem(Store store, DrugRequest drugRequest) {

//        Store store = storeService.getStoreWithNoFoundDetection(patientDrugs.getStoreId());
        if (!drugRequest.getDrugItems().isEmpty()) {
            Patient thePatient = null;

            if (!drugRequest.getIsWalkin()) {
                thePatient = patientService.findPatientOrThrow(drugRequest.getPatientNumber());
            }
            final Patient patient = thePatient;

            drugRequest.getDrugItems()
                    .stream()
                    .forEach(drugData -> {
                        DispensedDrug drugs = new DispensedDrug();
                        Item item = billingService.getItemByCode(drugData.getItemCode());
                        drugs.setPatient(patient);
                        drugs.setDrug(item);
                        drugs.setStore(store);
                        drugs.setDispensedDate(drugRequest.getDispenseDate());
                        drugs.setTransactionId(drugRequest.getTransactionId());
                        drugs.setQtyIssued(drugData.getQuantity());
                        drugs.setPrice(drugData.getPrice());
                        drugs.setAmount(drugData.getAmount());
                        drugs.setUnits(drugData.getUnit());
                        drugs.setDoctorName(drugData.getDoctorName());
                        drugs.setPaid(false);
                        drugs.setIsReturn(Boolean.FALSE);
                        drugs.setCollected(true);
                        drugs.setDispensedBy(SecurityUtils.getCurrentUserLogin().orElse(""));
                        drugs.setCollectedBy("");
                        drugs.setInstructions(drugData.getInstructions());
                        drugs.setOtherReference(drugRequest.getPatientNumber() + " " + drugRequest.getPatientName());
                        drugs.setWalkinFlag(drugRequest.getIsWalkin());

                        DispensedDrug savedDrug = repository.saveAndFlush(drugs);
                        doStockEntries(savedDrug.getId());
                        fulfillDocRequest(drugData.getRequestId());
                    });
        }
    }

    @Transactional
    public String dispense(DrugRequest drugRequest) {
        Store store = storeService.getStoreWithNoFoundDetection(drugRequest.getStoreId());
        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        drugRequest.setTransactionId(trdId);
        //
        if (drugRequest.getIsWalkin() && drugRequest.getPatientNumber() == null) {
            WalkIn w = createWalking(drugRequest.getPatientName());
            drugRequest.setPatientNumber(w.getWalkingIdentitificationNo());
            drugRequest.setPaymentMode("Cash");
        }

        billingService.save(toBill(drugRequest, store));

        dispenseItem(store, drugRequest);

        requestEventPublisher.publishUpdateEvent(DoctorRequestData.RequestType.Pharmacy);

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

    public Page<DispensedDrug> findDispensedDrugs(String transactionNo, String visitNo, String patientNo, String prescriptionNo, String billNo, Boolean isReturn, Pageable page) {
      
        Specification<DispensedDrug> spec = DispensingSpecification.createSpecification(transactionNo, visitNo, patientNo, prescriptionNo, billNo, isReturn);

        return repository.findAll(spec, page);

    }

    public List<DispensedDrug> returnItems(String visitNumber, List<ReturnedDrugData> returnedDrugs) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        List<DispensedDrug> returnedArray = new ArrayList();
        if (!returnedDrugs.isEmpty()) {
            returnedDrugs
                    .stream()
                    .forEach(drugData -> {
                        DispensedDrug drugs = findDispensedDrugOrThrow(drugData.getDrugId());
                        DispensedDrug drug1 = ObjectUtils.clone(drugs);
                        drug1.setAmount(-1 * (drugData.getQuantity()) * (drugs.getPrice()));
                        drug1.setQtyIssued(-1 * (drugData.getQuantity()));
                        drug1.setCollectedBy("");
                        drug1.setReturnDate(LocalDate.now());
                        drug1.setReturnReason(drugData.getReason());
                        drug1.setId(null);
                        returnedArray.add(drug1);
                    });

        }
        return repository.saveAll(returnedArray);
    }

    private PatientBill toBill(DrugRequest data, Store store) {

        //get the service point from store
        final ServicePoint srvpoint = store.getServicePoint();
        PatientBill patientbill = new PatientBill();
        if (!data.getIsWalkin()) {
            Visit visit = billingService.findVisitEntityOrThrow(data.getVisitNumber());
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
        patientbill.setBillingDate(data.getDispenseDate());
        patientbill.setPaymentMode(data.getPaymentMode());
        patientbill.setTransactionId(data.getTransactionId());
        patientbill.setStatus(BillStatus.Draft);

        List<PatientBillItem> lineItems = data.getDrugItems()
                .stream()
                .map(lineData -> {

                    PatientBillItem billItem = new PatientBillItem();
                    Item item = getItemByCode(lineData.getItemCode());

                    billItem.setBillingDate(data.getDispenseDate());
                    billItem.setTransactionId(data.getTransactionId());

                    billItem.setServicePointId(srvpoint.getId());
                    billItem.setServicePoint(srvpoint.getName());

                    billItem.setItem(item);
                    billItem.setPrice(lineData.getPrice());
                    billItem.setQuantity(lineData.getQuantity());
                    billItem.setAmount(lineData.getAmount());
                    billItem.setDiscount(lineData.getDiscount());
                    billItem.setBalance(lineData.getAmount());
                    billItem.setPaid(data.getPaymentMode() != null ? data.getPaymentMode().equals("Insurance") : false);
//                    billItem.setServicePoint(lineData.getServicePoint());
//                    billItem.setServicePointId(lineData.getServicePointId());
                    billItem.setStatus(BillStatus.Draft);
                    System.err.println(billItem);
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

    private void fulfillDocRequest(Long id) {
        if (id == null) {
            return;
        }
        DoctorRequest req = doctorRequestRepository.findById(id).orElse(null);
        if (req != null) {
            req.setFulfillerStatus(FullFillerStatusType.Fulfilled);
            doctorRequestRepository.save(req);
        }
    }

    private WalkIn createWalking(String patientName) {
        WalkIn w = new WalkIn();
        w.setFirstName(patientName);
        w.setSurname("WI");
        return walkingService.createWalking(w);
    }
}
