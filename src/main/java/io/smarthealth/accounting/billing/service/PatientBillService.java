package io.smarthealth.accounting.billing.service;

import io.smarthealth.accounting.billing.data.PatientBillData;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.PatientBillRepository;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.domain.specification.BillingSpecification;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.sequence.SequenceService;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
public class PatientBillService {

    private final PatientBillRepository patientBillRepository;
    private final VisitService visitService;
    private final ItemService itemService;
    private final SequenceService sequenceService;

    public PatientBillService(PatientBillRepository patientBillRepository, VisitService visitService, ItemService itemService, SequenceService sequenceService) {
        this.patientBillRepository = patientBillRepository;
        this.visitService = visitService;
        this.itemService = itemService;
        this.sequenceService = sequenceService;
    }

    public PatientBill createPatientBill(PatientBillData data) {
        //check the validity of the patient visit
        Visit visit = visitService.findVisitEntityOrThrow(data.getVisitNumber());

        PatientBill patientbill = new PatientBill();
        patientbill.setVisit(visit);
        patientbill.setPatient(visit.getPatient());
        patientbill.setAmount(data.getAmount());
        patientbill.setBalance(data.getAmount());
        patientbill.setBillNumber(data.getBillNumber());
        patientbill.setBillingDate(data.getBillingDate());
        patientbill.setJournalNumber(data.getJournalNumber());
        patientbill.setPaymentMode(data.getPaymentMode());
        patientbill.setReferenceNumber(data.getReferenceNumber());
        patientbill.setStatus(BillStatus.Draft);

        List<PatientBillItem> lineItems = data.getBillLines()
                .stream()
                .map(lineData -> {
                    PatientBillItem billLine = new PatientBillItem();

                    billLine.setBillingDate(lineData.getBillingDate());
                    billLine.setTransactionNo(lineData.getTransactionNo());

                    if (lineData.getItemId() != null) {
                        Item item = itemService.findItemEntityOrThrow(lineData.getItemId());
                        billLine.setItem(item);

                    }

                    billLine.setPrice(lineData.getPrice());
                    billLine.setQuantity(lineData.getQuantity());
                    billLine.setAmount(lineData.getAmount());
                    billLine.setBalance(lineData.getAmount());
                    billLine.setServicePointId(lineData.getServicePointId());
                    billLine.setServicePoint(lineData.getServicePoint());
                    billLine.setStatus(BillStatus.Draft);

                    return billLine;
                })
                .collect(Collectors.toList());
        patientbill.addPatientBillLine(lineItems);

        PatientBill savedBill = patientBillRepository.save(patientbill);
        return savedBill;
    }

    public Optional<PatientBill> findBillByBillNumber(final String billNumber) {
        return patientBillRepository.findByBillNumber(billNumber);
    }
   public PatientBill findOneWithNoFoundDetection(Long id){
       return patientBillRepository.findById(id)
               .orElseThrow(()-> APIException.notFound("Patien Bill with Id {0} not found",id));
   }
    public Page<PatientBill> findAllBills(String refNo, String visitNo, String patientNo, String paymentMode, String billNo, String status, Pageable page) {
        BillStatus state = BillStatus.valueOf(status);
        Specification<PatientBill> spec = BillingSpecification.createSpecification(refNo, visitNo, patientNo,paymentMode, billNo, state);

        return patientBillRepository.findAll(spec, page);

    }
 
}
