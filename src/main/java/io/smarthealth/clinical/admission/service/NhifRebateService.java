package io.smarthealth.clinical.admission.service;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.clinical.admission.data.NhifRebateData;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.domain.NhifRebate;
import io.smarthealth.clinical.admission.domain.repository.NhifRebateRepository;
import io.smarthealth.clinical.admission.domain.specification.NhifRebateSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Service
@RequiredArgsConstructor
public class NhifRebateService {

    private final NhifRebateRepository rebateRepository;
    private final PatientService patientService;
    private final AdmissionService admissionService;
    private final BillingService billingService;

    public NhifRebate createNhifRebate(NhifRebateData data) {
        NhifRebate rebate = data.map();
        Patient patient = patientService.findPatientOrThrow(data.getPatientNumber());
        Admission admission = admissionService.findAdmissionByNumber(data.getAdmissionNumber());
        isNhifRebateSet(admission);
        rebate.setAdmission(admission);
        rebate.setPatient(patient);
        PatientBill bill = billingService.createFee(admission.getAdmissionNo(),ItemCategory.NHIF_Rebate, data.getDuration());
        rebate.setRate(bill.getBillItems().get(0).getPrice());
        rebate.setAmount(bill.getAmount());
        return rebateRepository.save(rebate);
    }

    public Page<NhifRebate> fetchAllNhifRebates(Pageable page) {
        return rebateRepository.findAll(page);
    }

    public Page<NhifRebate> fetchNhifRebates(String admissionNumber, String patientNumber, String memberNumber, DateRange range, Pageable page) {
        Specification<NhifRebate> spec = NhifRebateSpecification.createSpecification(admissionNumber, patientNumber, memberNumber, range);
        return rebateRepository.findAll(spec, page);
    }

    

    public NhifRebate getNhifRebate(Long id) {
        return rebateRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("NhifRebate with id  {0} not found.", id));
    }
    
    public void isNhifRebateSet(Admission admission) {
        Optional<NhifRebate> rebate = rebateRepository.findByAdmission(admission);
        if(rebate.isPresent())
            throw APIException.conflict("Nhif Rebate already set for this admission {0}", admission.getAdmissionNo());
    }

    public NhifRebate updateNhifRebate(NhifRebate rebate) {
        return rebateRepository.save(rebate);
    }
}
