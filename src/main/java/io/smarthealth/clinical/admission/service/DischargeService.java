package io.smarthealth.clinical.admission.service;

import io.smarthealth.clinical.admission.data.DischargeData;
import io.smarthealth.clinical.admission.data.DischargeDiagnosis;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.domain.DischargeSummary;
import io.smarthealth.clinical.admission.domain.repository.AdmissionRepository;
import io.smarthealth.clinical.admission.domain.repository.DischargeSummaryRepository;
import io.smarthealth.clinical.admission.domain.specification.DischargeSummarySpecification;
import io.smarthealth.clinical.record.data.DiagnosisData;
import io.smarthealth.clinical.record.domain.Diagnosis;
import io.smarthealth.clinical.record.domain.PatientDiagnosis;
import io.smarthealth.clinical.record.domain.PatientDiagnosisRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Service
@RequiredArgsConstructor
public class DischargeService {

    private final SequenceNumberService sequenceNumberService;
    private final AdmissionRepository admissionRepository;
    private final DischargeSummaryRepository repository;
    private final PatientDiagnosisRepository patientDiagnosisRepository;

    @Transactional
    public DischargeSummary createDischarge(DischargeData data) {
        Admission admission =getAdmission(data.getAdmissionNumber());

        String dischargeNo = sequenceNumberService.next(1l, Sequences.DischargeNumber.name());
        DischargeSummary discharge = new DischargeSummary();
        discharge.setAdmission(admission);
        discharge.setPatient(admission.getPatient());
        discharge.setDiagnosis(data.getDiagnosis());
        discharge.setDischargeDate(data.getDischargeDate());
        discharge.setDischargeMethod(data.getDischargeMethod());
        discharge.setDischargeNo(dischargeNo);
        discharge.setDischargedBy(data.getDischargedBy());
        discharge.setInstructions(data.getInstructions());
        discharge.setOutcome(data.getOutcome());

        //update the discharge
        admission.setDischargeDate(discharge.getDischargeDate());
        admission.setDischarged(Boolean.TRUE);
        admission.setDischargedBy(discharge.getDischargedBy());

        admissionRepository.save(admission);

        return repository.save(discharge);
    }

    public Page<DischargeSummary> getDischarges(final String dischargeNo, String patientNo, String term, DateRange range, final Pageable pageable) {
        Specification<DischargeSummary> s = DischargeSummarySpecification.createSpecification(dischargeNo, patientNo, term, range);
        return repository.findAll(s, pageable);
    }

    public DischargeSummary getDischargeById(Long id) {
        if (id != null) {
            return repository.findById(id).orElseThrow(() -> APIException.notFound("DischargeSummary id {0} not found", id));
        } else {
            throw APIException.badRequest("Please provide dischrage id ", "");
        }
    }

    public DischargeSummary getDischargeByNumber(String admissionNo) {
        if (admissionNo != null) {
            return repository.findByDischargeNo(admissionNo).orElseThrow(() -> APIException.notFound("DischargeSummary with number {0} not found", admissionNo));
        } else {
            throw APIException.badRequest("Please provide admission number ", "");
        }
    }
    public Admission getAdmission(String admissionNo){
            Admission admission = admissionRepository.findByAdmissionNo(admissionNo)
                .orElseThrow(() -> APIException.notFound("Admission with  Number {} Not Found", admissionNo));
            
            if(admission.getDischarged()){
                throw APIException.badRequest("Admission Number {} already discharged", admissionNo);
            }

            return admission;
    }
@Transactional
    public DischargeSummary updateDischarge(Long id, DischargeData data) {
        DischargeSummary discharge = getDischargeById(id);

        Admission admission = admissionRepository.findByAdmissionNo(data.getAdmissionNumber())
                .orElseThrow(() -> APIException.notFound("Admission with  Number {} Not Found", data.getAdmissionNumber()));

        discharge.setAdmission(admission);
        discharge.setPatient(admission.getPatient());
        discharge.setDiagnosis(data.getDiagnosis());
        discharge.setDischargeDate(data.getDischargeDate());
        discharge.setDischargeMethod(data.getDischargeMethod());
        discharge.setDischargedBy(data.getDischargedBy());
        discharge.setInstructions(data.getInstructions());
        discharge.setOutcome(data.getOutcome());

        admission.setDischargeDate(discharge.getDischargeDate());
        admission.setDischarged(Boolean.TRUE); 
        admission.setDischargedBy(discharge.getDischargedBy());
        admissionRepository.save(admission);

        return repository.save(discharge);
    }
    @Transactional
    public PatientDiagnosis updateDiagnosis(String admissionNo,  DischargeDiagnosis data){
       PatientDiagnosis diagnosis = new PatientDiagnosis();
       
        Visit admission = admissionRepository.findByAdmissionNo(admissionNo).orElseThrow(() -> APIException.notFound("Admission with Number {0} Not Found", admissionNo));
       if(!admissionNo.equals(data.getAdmissionNumber())){
           throw APIException.badRequest("Admission Number {0}  is not same as request object admission number {1} ", admissionNo,data.getAdmissionNumber());
       }
       diagnosis.setPatient(admission.getPatient());
       diagnosis.setVisit(admission);
       diagnosis.setCertainty(data.getCertainty());
        Diagnosis diag=new Diagnosis();
        diag.setCode(data.getCode());
        diag.setDescription(data.getDescription());
        diagnosis.setDoctor(data.getDoctor());
        
       diagnosis.setDiagnosis(diag);
       diagnosis.setDiagnosisOrder(data.getDiagnosisOrder());
       diagnosis.setIsCondition(Boolean.FALSE);
       diagnosis.setNotes(data.getRemarks());
       diagnosis.setDateRecorded(data.getDiagnosisDate().atStartOfDay());
       
       return patientDiagnosisRepository.save(diagnosis);
    }
}
