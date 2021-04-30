package io.smarthealth.clinical.pharmacy.service;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.PatientBillItemRepository;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.domain.ServicePointRepository;
import io.smarthealth.clinical.pharmacy.data.DrugRequest;
import io.smarthealth.clinical.pharmacy.data.ReturnedDrugData;
import io.smarthealth.clinical.pharmacy.domain.DispensedDrug;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
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
import io.smarthealth.clinical.pharmacy.domain.DispensedDrugsInterface;
import io.smarthealth.clinical.pharmacy.domain.specification.DispensingSpecification;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.domain.DoctorsRequestRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.person.domain.WalkIn;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.organization.person.service.WalkingService;
import io.smarthealth.security.util.SecurityUtils;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.inventory.events.InventoryEvent;
import io.smarthealth.stock.inventory.service.InventoryService;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemRepository;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.domain.StoreRepository;
import io.smarthealth.stock.stores.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class DispensingService {

    private final DispensedDrugRepository dispensedDrugRepository;
    private final ItemRepository itemRepository;
    private final PatientService patientService;
    private final StoreRepository storeRepository;
    private final StoreService storeService;
    private final BillingService billingService;
    private final InventoryService inventoryService;
    private final SequenceNumberService sequenceNumberService;
    private final VisitService visitService;
    private final DoctorsRequestRepository doctorRequestRepository;
    private final WalkingService walkingService;
    private final ServicePointRepository servicePointRepository;
    private final PatientBillItemRepository billItemRepository;


    public List<DispensedDrug> dispenseItem(DrugRequest drugRequest, Store store) {
        List<DispensedDrug> dispensedDrugList = new ArrayList<>();
        Visit visit = visitService.findVisit(drugRequest.getVisitNumber()).orElse(null);
//        Store store = storeService.getStoreWithNoFoundDetection(patientDrugs.getStoreId());
        if (!drugRequest.getDrugItems().isEmpty()) {
            Patient thePatient = null;

            if (!drugRequest.getIsWalkin()) {
                thePatient = patientService.findPatientOrThrow(drugRequest.getPatientNumber());
            }
            final Patient patient = thePatient;

            drugRequest.getDrugItems()
                    .stream()
                    .map(drugData -> {
                        DispensedDrug dispensedDrug = new DispensedDrug();
                        Item item = billingService.getItemByCode(drugData.getItemCode());

                        dispensedDrug.setPatient(patient);
                        dispensedDrug.setDrug(item);
                        dispensedDrug.setStore(store);
                        dispensedDrug.setDispensedDate(drugRequest.getDispenseDate());
                        dispensedDrug.setTransactionId(drugRequest.getTransactionId());
                        dispensedDrug.setQtyIssued(drugData.getQuantity());
                        dispensedDrug.setPrice(drugData.getPrice());
                        dispensedDrug.setAmount((drugData.getPrice() * drugData.getQuantity()));
                        dispensedDrug.setUnits(drugData.getUnit());
                        dispensedDrug.setDoctorName(drugData.getDoctorName());
                        dispensedDrug.setPaid(false);
                        dispensedDrug.setIsReturn(Boolean.FALSE);
                        dispensedDrug.setReturnedQuantity(0D);
                        dispensedDrug.setCollected(true);
                        dispensedDrug.setDispensedBy(SecurityUtils.getCurrentUserLogin().orElse(""));
                        dispensedDrug.setCollectedBy("");
                        dispensedDrug.setInstructions(drugData.getInstructions());
                        dispensedDrug.setOtherReference(drugRequest.getPatientNumber() + " " + drugRequest.getPatientName());
                        dispensedDrug.setWalkinFlag(drugRequest.getIsWalkin());
                        dispensedDrug.setBatchNumber(drugData.getBatchNumber());
                        dispensedDrug.setDeliveryNumber(drugData.getBatchNumber());
                        dispensedDrug.setVisit(visit);
                        dispensedDrug.setBillNumber(drugRequest.getBillNumber());
                        //dispensedDrug.setBillItem(patientBillItem);

                        DispensedDrug savedDrug = dispensedDrugRepository.saveAndFlush(dispensedDrug);
                        doStockEntries(savedDrug.getId());
                        fulfillDocRequest(drugData.getRequestId());
                        return savedDrug;
                    })
                    .collect(Collectors.toList())
                    .forEach(dispensedDrugList::add);
        }
        return dispensedDrugList;
    }

    public void dispenseConsumables(List<PatientBillItem> consumables, MovementType movementType, MovementPurpose movementPurpose) {
        if (consumables.isEmpty()) return;
        consumables.stream()
                .forEach(consumable -> {
                    if (consumable.getServicePointId() == null) {
                        throw APIException.badRequest("Inventory Store is Required for the Service Point");
                    }
                    ServicePoint servicePoint = servicePointRepository.findById(consumable.getServicePointId())
                            .orElseThrow(() -> APIException.notFound("Service Point with Id {} not found", consumable.getServicePointId()));

                    Store store = Optional.of(servicePoint.getStore())
                            .orElseThrow(() -> APIException.badRequest("Item Inventory Location for the service point {} not configured. Item requires Store Location.", servicePoint.getName()));

                    StockEntry stock = new StockEntry();
                    stock.setAmount(NumberUtils.toScaledBigDecimal((consumable.getQuantity()) * (consumable.getPrice())));
                    stock.setDeliveryNumber(consumable.getTransactionId());
                    stock.setQuantity(consumable.getQuantity());
                    stock.setItem(consumable.getItem());
                    stock.setMoveType(movementType);
                    stock.setPrice(NumberUtils.createBigDecimal(String.valueOf(consumable.getPrice())));
                    stock.setPurpose(movementPurpose);
                    if (consumable.getPatientBill().getWalkinFlag()) {
                        stock.setReferenceNumber(consumable.getPatientBill().getReference());
                    } else {
                        stock.setReferenceNumber(consumable.getPatientBill().getPatient().getPatientNumber());
                    }
                    stock.setStore(store);
                    stock.setTransactionDate(LocalDate.now());
                    stock.setTransactionNumber(consumable.getTransactionId());
                    inventoryService.save(stock);
                });
    }

    @Transactional
    public String dispense(DrugRequest drugRequest) {
//        Visit visit = visitService.findVisitEntityOrThrow(drugRequest.getVisitNumber());
        Store store = storeService.getStoreWithNoFoundDetection(drugRequest.getStoreId());
        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        drugRequest.setTransactionId(trdId);

        //
        if (drugRequest.getIsWalkin() && drugRequest.getPatientNumber() == null) {
            WalkIn w = createWalking(drugRequest.getPatientName());
            drugRequest.setPatientNumber(w.getWalkingIdentitificationNo());
            drugRequest.setPaymentMode("Cash");
        }

        PatientBill savedBill = billingService.save(toBill(drugRequest, store));

        drugRequest.setBillNumber(savedBill.getBillNumber());

        dispenseItem(drugRequest, store);

        //if all goes well and the patient was sent on this service point direct (exclusive of doctor request) - mark on the patient visit the patient has been served, and remove from the waiting list
        if (!drugRequest.getIsWalkin()) {
            Visit visit = visitService.findVisitEntityOrThrow(drugRequest.getVisitNumber());
            if (visit.getServiceType().equals(VisitEnum.ServiceType.Other)) {
                visit.setServedAtServicePoint(Boolean.TRUE);
                visitService.createAVisit(visit);
            }
        }

        return trdId;
    }

    private void doStockEntries(Long drugId) {
        Optional<DispensedDrug> drug = dispensedDrugRepository.findById(drugId);
        if (drug.isPresent()) {
            inventoryService.save(StockEntry.create(drug.get()));
        } else {
            System.err.println("Drug entry is empty");
        }
    }

    public DispensedDrug findDispensedDrugOrThrow(Long id) {
        return dispensedDrugRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Dispensed Drug with id {0} not found", id));
    }

    public Page<DispensedDrug> findDispensedDrugs(String transactionNo, String visitNo, String patientNo, String prescriptionNo,
                                                  String billNo, Boolean isReturn, DateRange range, Pageable page) {

        Specification<DispensedDrug> spec = DispensingSpecification.createSpecification(transactionNo, visitNo, patientNo,
                prescriptionNo, billNo, isReturn, range);

        return dispensedDrugRepository.findAll(spec, page);
    }

    public boolean UpdateFullfillerStatus(Long id) {
        try {
//            fulfillDocRequest(id);
            DoctorRequest req = doctorRequestRepository.findById(id).orElse(null);
            if (req != null) {
                req.setVoided(Boolean.TRUE);
                req.setFulfillerStatus(FullFillerStatusType.Fulfilled);
                req.setNotes("Voided for a reason");
                doctorRequestRepository.save(req);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error deleting Patient drugs with id " + id, e.getMessage());
        }
    }

    @Transactional
    public List<DispensedDrug> returnItems(String visitNumber, List<ReturnedDrugData> returnedDrugs) {
        System.err.println("Retugint the ites, ..... ");
        returnedDrugs.forEach(x -> System.out.println("daddy ... " + x.getDrugId()));

        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        List<DispensedDrug> dispensedDrugsList = new ArrayList();
        if (!returnedDrugs.isEmpty()) {
            returnedDrugs
                    .stream()
                    .forEach(drugData -> {
                        StockEntry stock = new StockEntry();

                        DispensedDrug drugs = findDispensedDrugOrThrow(drugData.getDrugId());

                        DispensedDrug drug1 = ObjectUtils.clone(drugs);
                        drug1.setAmount(-1 * (drugData.getQuantity()) * (drugs.getPrice()));
                        drug1.setQtyIssued(-1 * (drugData.getQuantity()));
                        drug1.setCollectedBy(SecurityUtils.getCurrentUserLogin().orElse(""));
                        drug1.setIsReturn(Boolean.TRUE);
                        drug1.setReturnDate(LocalDate.now());
                        drug1.setReturnReason(drugData.getReason());
                        drug1.setId(null);
                        drug1.setDispensedBy(SecurityUtils.getCurrentUserLogin().orElse(""));
                        drug1.setVisit(visit);

                        dispensedDrugsList.add(drug1);

                        stock.setAmount(NumberUtils.toScaledBigDecimal((drugData.getQuantity()) * (drugs.getPrice())));
                        stock.setDeliveryNumber(drug1.getOtherReference());
                        stock.setQuantity(drugData.getQuantity());
                        stock.setItem(drug1.getDrug());
                        stock.setMoveType(MovementType.Returns);
                        stock.setPrice(NumberUtils.createBigDecimal(String.valueOf(drug1.getAmount())));
                        stock.setPurpose(MovementPurpose.Returns);
                        stock.setReferenceNumber(drug1.getOtherReference());
                        stock.setStore(drug1.getStore());
                        stock.setTransactionDate(LocalDate.now());
                        stock.setTransactionNumber(trdId);
                        stock.setUnit(drug1.getUnits());
                        inventoryService.doStockEntry(InventoryEvent.Type.Increase, stock, drug1.getStore(), drug1.getDrug(), drugData.getQuantity());

                        drugs.setReturnedQuantity((drugs.getReturnedQuantity() + drugData.getQuantity()));
                        drugs.setReturnReason(drugData.getReason());
                        drugs.setReturnDate(drugData.getReturnDate() != null ? drugData.getReturnDate() : LocalDate.now());

                        dispensedDrugRepository.save(drugs);

//                        //update billing details
//                        PatientBillItem patientBillItem =
//                                billingService.findBillItemById(drugData.getPatientBillItemId());
//
//                        if (!patientBillItem.isFinalized()) {
//                            Double newQuantity = (patientBillItem.getQuantity() - drugData.getQuantity());
//                            patientBillItem.setQuantity(newQuantity);
//                            patientBillItem.setAmount((newQuantity * patientBillItem.getPrice()));
//                            patientBillItem.setBalance((patientBillItem.getBalance() - (patientBillItem.getPrice() - drugData.getQuantity())));
//                            billItemRepository.save(patientBillItem);
//
//                            PatientBill patientBill = patientBillItem.getPatientBill();
//                            patientBill.setAmount((patientBill.getAmount() - (patientBillItem.getPrice() - drugData.getQuantity())));
//                            patientBill.setBalance((patientBill.getBalance() - (patientBillItem.getPrice() - drugData.getQuantity())));
//                            billingService.update(patientBill);
//                        }
                    });

        }

        return dispensedDrugRepository.saveAll(dispensedDrugsList);
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
            Optional<WalkIn> wi = walkingService.fetchWalkingByWalkingNo(data.getPatientNumber());
            if (wi.isPresent()) {
                patientbill.setOtherDetails(wi.get().getFullName());
            } else {
                patientbill.setOtherDetails(data.getPatientName());
            }
            patientbill.setReference(data.getPatientNumber());
//            patientbill.setOtherDetails(data.getPatientName());
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
                    billItem.setBillPayMode(lineData.getPaymentMethod());
                    billItem.setStatus(BillStatus.Draft);
                    billItem.setEntryType(lineData.getEntryType());
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

    public List<DispensedDrugsInterface> dispensedDrugs(DateRange range) {
        return dispensedDrugRepository.dispensedDrugs(range.getStartDate(), range.getEndDate());
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
        w.setSurname("");
        return walkingService.createWalking(w);
    }

    public List<DispensedDrug> findDispensedDrugs(Long drugId, String visitNo, LocalDate date, String transNo) {
        return dispensedDrugRepository.findDispensedDrug(drugId, visitNo, date, transNo);
    }

}
