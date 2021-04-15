package io.smarthealth.clinical.admission.service;

import io.smarthealth.clinical.admission.data.DischargeData;
import io.smarthealth.clinical.admission.data.DischargeDiagnosis;
import io.smarthealth.clinical.admission.data.DischargeSummaryReport;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.domain.DischargeSummary;
import io.smarthealth.clinical.admission.domain.repository.AdmissionRepository;
import io.smarthealth.clinical.admission.domain.repository.DischargeSummaryRepository;
import io.smarthealth.clinical.admission.domain.specification.DischargeSummarySpecification;
import io.smarthealth.clinical.laboratory.service.LaboratoryService;
import io.smarthealth.clinical.procedure.service.ProcedureService;
import io.smarthealth.clinical.radiology.domain.PatientScanTest;
import io.smarthealth.clinical.radiology.service.RadiologyService;
import io.smarthealth.clinical.record.data.DiagnosisData;
import io.smarthealth.clinical.record.data.PatientPrescription;
import io.smarthealth.clinical.record.domain.Diagnosis;
import io.smarthealth.clinical.record.domain.PatientDiagnosis;
import io.smarthealth.clinical.record.domain.PatientDiagnosisRepository;
import io.smarthealth.clinical.record.domain.PrescriptionRepository;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Kennedy.Ikatanyi
 */
@Service
@RequiredArgsConstructor
public class DischargeService {

    private final SequenceNumberService sequenceNumberService;
    private final AdmissionRepository admissionRepository;
    private final DischargeSummaryRepository repository;
    private final PatientDiagnosisRepository patientDiagnosisRepository;

    private final RadiologyService radiologyService;
    private final ProcedureService procedureService;
    private final LaboratoryService labService;
    private final PrescriptionRepository prescriptionRepository;
    private final PatientDiagnosisRepository diagnosisRepository;

    @Transactional
    public DischargeSummary createDischarge(DischargeData data) {
        Admission admission = getAdmission(data.getAdmissionNumber());

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
        admission.setStatus(VisitEnum.Status.Discharged);
        admission.setDischargeNo(dischargeNo);
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

    public DischargeSummary getDischargeByVisit(String admissionNo) {
        if (admissionNo == null) {
            throw APIException.badRequest("Admission Number is Required");
        }
        return repository.findDischargeByVisitNo(admissionNo).orElse(null);

    }

    public DischargeSummary getDischargeByNumber(String admissionNo) {
        if (admissionNo != null) {
            return repository.findByDischargeNo(admissionNo).orElseThrow(() -> APIException.notFound("DischargeSummary with number {0} not found", admissionNo));
        } else {
            throw APIException.badRequest("Please provide admission number ", "");
        }
    }

    public Admission getAdmission(String admissionNo) {
        if (admissionNo == null) {
            return null;
        }
        Admission admission = admissionRepository.findByAdmissionNo(admissionNo)
                .orElseThrow(() -> APIException.notFound("Admission with  Number {} Not Found", admissionNo));

        if (admission.getDischarged()) {
            throw APIException.badRequest("Admission Number {} already discharged", admissionNo);
        }

        return admission;
    }

    public DischargeSummary getDischargeByAdmission(Admission admission) {
        return repository.findByAdmission(admission).orElseThrow(() -> APIException.notFound("DischargeSummary with Admission Number {0} not found", admission.getAdmissionNo()));

    }

    @Transactional
    public DischargeSummary updateDischarge(Long id, DischargeData data) {
        DischargeSummary discharge = getDischargeById(id);

        Admission admission = discharge.getAdmission();

        discharge.setAdmission(admission);
        discharge.setPatient(admission.getPatient());
        discharge.setDiagnosis(data.getDiagnosis());
        discharge.setDischargeDate(data.getDischargeDate());
        discharge.setDischargeMethod(data.getDischargeMethod());
        discharge.setDischargedBy(data.getDischargedBy());
        discharge.setInstructions(data.getInstructions());
        discharge.setOutcome(data.getOutcome());
        if(data.getReviewDate()!=null){
            discharge.setReviewDate(data.getReviewDate());
        }

        admission.setDischargeDate(discharge.getDischargeDate());
        admission.setDischarged(Boolean.TRUE);
        admission.setDischargedBy(discharge.getDischargedBy());
        admissionRepository.save(admission);

        return repository.save(discharge);
    }

    @Transactional
    public DischargeSummary updatePartialDischarge(Long id, DischargeData data) {
        DischargeSummary discharge = getDischargeById(id);

        Admission admission = discharge.getAdmission();
        if(data.getDiagnosis()!=null) {
            discharge.setDiagnosis(data.getDiagnosis());
        }
        if(data.getDischargeDate()!=null) {
            discharge.setDischargeDate(data.getDischargeDate());
            admission.setDischargeDate(discharge.getDischargeDate());
        }
        if(data.getDischargeMethod()!=null) {
            discharge.setDischargeMethod(data.getDischargeMethod());
        }
        if(data.getDischargedBy()!=null) {
            discharge.setDischargedBy(data.getDischargedBy());
            admission.setDischargedBy(discharge.getDischargedBy());
        }
        if(data.getInstructions()!=null) {
            discharge.setInstructions(data.getInstructions());
        }
        if(data.getOutcome()!=null) {
            discharge.setOutcome(data.getOutcome());
        }

        if(data.getReviewDate()!=null){
            discharge.setReviewDate(data.getReviewDate());
        }

        admission.setDischarged(Boolean.TRUE);

        admissionRepository.save(admission);

        return repository.save(discharge);
    }

    @Transactional
    public PatientDiagnosis patchDiagnosis(String admissionNo, DischargeDiagnosis data) {
        PatientDiagnosis diagnosis = patientDiagnosisRepository.findPatientDiagnosisByVisitNumber(admissionNo)
                .stream().findFirst().orElse(new PatientDiagnosis());

        Optional<Admission> admissionOptional = admissionRepository.findByAdmissionNo(admissionNo);

        if(admissionOptional.isPresent()){
            Admission admission = admissionOptional.get();
            diagnosis.setPatient(admission.getPatient());
            diagnosis.setVisit(admission);
            if(data.getCertainty()!=null) {
                diagnosis.setCertainty(data.getCertainty());
            }

            Diagnosis diag = Optional.of(diagnosis.getDiagnosis()).orElse(new Diagnosis());
            diag.setCode(data.getCode());
            diag.setDescription(data.getDescription());
            if(data.getDoctor()!=null) {
                diagnosis.setDoctor(data.getDoctor());
            }
            diagnosis.setDiagnosis(diag);
            if(data.getDiagnosisOrder()!=null) {
                diagnosis.setDiagnosisOrder(data.getDiagnosisOrder());
            }
            diagnosis.setIsCondition(Boolean.FALSE);
            if(data.getRemarks()!=null) {
                diagnosis.setNotes(data.getRemarks());
            }
            if(data.getDiagnosisDate()!=null) {
                diagnosis.setDateRecorded(data.getDiagnosisDate().atStartOfDay());
            }

            return patientDiagnosisRepository.save(diagnosis);
        }
        return diagnosis;

    }
    @Transactional
    public PatientDiagnosis updateDiagnosis(String admissionNo, DischargeDiagnosis data) {
        PatientDiagnosis diagnosis;
        if(data.getId()!=null){
            diagnosis = patientDiagnosisRepository.findById(data.getId()).orElse(new PatientDiagnosis());
        }else {
            diagnosis = new PatientDiagnosis();
        }

        Visit admission = admissionRepository.findByAdmissionNo(admissionNo).orElseThrow(() -> APIException.notFound("Admission with Number {0} Not Found", admissionNo));
        if (!admissionNo.equals(data.getAdmissionNumber())) {
            throw APIException.badRequest("Admission Number {0}  is not same as request object admission number {1} ", admissionNo, data.getAdmissionNumber());
        }
        diagnosis.setPatient(admission.getPatient());
        diagnosis.setVisit(admission);
        if(data.getCertainty()!=null) {
            diagnosis.setCertainty(data.getCertainty());
        }
        Diagnosis diag = Optional.of(diagnosis.getDiagnosis()).orElse(new Diagnosis());
        if(data.getCode()!=null) {
            diag.setCode(data.getCode());
        }
        if(data.getDescription()!=null) {
            diag.setDescription(data.getDescription());
        }
        if(data.getDoctor()!=null) {
            diagnosis.setDoctor(data.getDoctor());
        }

        diagnosis.setDiagnosis(diag);
        if(data.getDiagnosisOrder()!=null) {
            diagnosis.setDiagnosisOrder(data.getDiagnosisOrder());
        }
        diagnosis.setIsCondition(Boolean.FALSE);
        if(data.getRemarks()!=null) {
            diagnosis.setNotes(data.getRemarks());
        }
        if(data.getDiagnosisDate()!=null) {
            diagnosis.setDateRecorded(data.getDiagnosisDate().atStartOfDay());
        }
        return patientDiagnosisRepository.save(diagnosis);
    }

    public DischargeSummary saveDischargeSummary(DischargeSummary discharge) {
        return repository.save(discharge);
    }

    public List<DischargeSummaryReport> getDischargeSummaryReport(String dischargeNo, String patientNo, String term, DateRange range, Pageable pageable) {

        return repository.findAll(DischargeSummarySpecification.createSpecification(dischargeNo, patientNo, term, range), pageable).stream()
                .map(x -> x.getDischargeNo())
                .map(d -> getDischargeSummaryReport(d, null))
                .collect(Collectors.toList());
    }

    public DischargeSummaryReport getDischargeSummaryReport(String dischargeNo, String admissionNo) {

        DischargeSummary discharge;
        if (dischargeNo == null) {
            discharge = repository.findDischargeByVisitNo(admissionNo).orElse(null);
        } else {
            discharge = repository.findByDischargeNo(dischargeNo).orElse(null);
        }
        DischargeSummaryReport data = new DischargeSummaryReport();


        if (discharge != null) {
            String visitNumber = discharge.getAdmission().getVisitNumber();

            data.setId(discharge.getId());
            data.setDoctor(discharge.getDoctor());
            data.setDiagnosis(discharge.getDiagnosis());
            data.setDischargeDate(discharge.getDischargeDate());
            data.setDischargeMethod(discharge.getDischargeMethod());
            data.setDischargeNo(discharge.getDischargeNo());
            data.setDischargedBy(discharge.getDischargedBy());
            data.setGender(discharge.getPatient().getGender().name());
            data.setInstructions(discharge.getInstructions());
            data.setOutcome(discharge.getOutcome());
            data.setPatientName(discharge.getPatient().getFullName());
            data.setPatientNumber(discharge.getPatient().getPatientNumber());
            data.setResidence(discharge.getPatient().getResidence());
            data.setAge(discharge.getPatient().getAge());
            data.setReviewDate(discharge.getReviewDate());
            if (discharge.getAdmission() != null) {
                data.setAdmissionDate(discharge.getAdmission().getAdmissionDate());
                data.setAdmissionNumber(discharge.getAdmission().getAdmissionNo());
                data.setPaymentMode(discharge.getAdmission().getPaymentMethod().name());
                if (discharge.getAdmission().getBed() != null) {
                    data.setBed(discharge.getAdmission().getBed().getName());
                }
                if (discharge.getAdmission().getWard() != null) {
                    data.setWard(discharge.getAdmission().getWard().getName());
                }
            }
            String radiologyTests = radiologyService.getPatientScansTestByVisit(visitNumber)
                    .stream()
                    .map(PatientScanTest::toData)
                    .map(test -> "* ".concat(test.getScanName()))
                    .collect(Collectors.joining("\n"));

            data.setRadiologySummary(radiologyTests);

            String procedureTests = procedureService.findProcedureResultsByVisit(discharge.getAdmission())
                    .stream()
                    .filter(x -> x.getProcedureTest().getCategory() == ItemCategory.Procedure)
                    .map(test -> test.toData())
                    .map(test -> "* ".concat(StringUtils.clean(test.getProcedureName())))
                    .collect(Collectors.joining("\n"));

            data.setProceduresSummary(procedureTests);

            String labTests = labService.getTestsResultsByVisit(visitNumber, null)
                    .stream()
                    .map(test -> test.toData(false))
                    .map(test -> "* ".concat(StringUtils.clean(test.getTestName())))
                    .collect(Collectors.joining("\n"));

            data.setLaboratorySummary(labTests);

            data.setPrescriptionList(
                    prescriptionRepository.findDischargePrescriptionByVisitNumber(visitNumber)
                            .stream()
                            .map(drug -> PatientPrescription.of(drug))
                    .collect(Collectors.toList())
            );
            data.setDiagnosisList(
                    diagnosisRepository.findPatientDiagnosisByVisitNumber(visitNumber).stream()
                            .map(diag -> DiagnosisData.map(diag))
                            .collect(Collectors.toList())
            );
        }

        return data;
    }
}
