package io.smarthealth.clinical.inpatient.admission.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.clinical.inpatient.admission.data.AdmissionData;
import io.smarthealth.clinical.inpatient.admission.data.CreateAdmission;
import io.smarthealth.clinical.inpatient.admission.domain.Admission;
import io.smarthealth.clinical.inpatient.admission.domain.AdmissionRepository;
import io.smarthealth.clinical.inpatient.setup.domain.Bed;
import io.smarthealth.clinical.inpatient.setup.service.BedService;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.domain.EmployeeRepository;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import io.smarthealth.security.util.SecurityUtils;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class AdmissionService {

    private final AdmissionRepository repository;
    private final PatientRepository patientRepository;
    private final SequenceNumberService sequenceNumberService;
    private final BedService bedService;
    private final EmployeeRepository employeeRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Admission createAdmission(CreateAdmission data) {
        Patient patient = getPatientOrThrow(data.getPatientNumber());
        Bed bed = bedService.getBedOrThrow(data.getBedId());
        Employee medic = data.getAdmittingDoctorId()!=null ? employeeRepository.findById(data.getAdmittingDoctorId()).orElse(null): null;

        Admission admission = new Admission();
        admission.setAdmissionDate(data.getAdmissionDate());
        admission.setAdmissionReason(data.getAdmissionReason());
        admission.setBed(bed);
        admission.setMedic(medic);
        admission.setPatient(patient);
        admission.setStatus(Admission.Status.Admitted);
        admission.setType(data.getAdmissionType());

        String admissionNo = sequenceNumberService.next(1L, Sequences.Admission.name());
        admission.setAdmissionNo(admissionNo);

        Admission savedAdmission = repository.save(admission);
        Bed occupiedBed = savedAdmission.getBed();
        occupiedBed.setStatus(Bed.Status.Occupied);
        bedService.save(occupiedBed);

        return savedAdmission;
    }

    public Optional<Admission> getAdmission(Long id) {
        return repository.findById(id);
    }

    public Admission getAdmissionOrThrow(Long id) {
        return getAdmission(id)
                .orElseThrow(() -> APIException.notFound("Admission with ID {0} Not Found", id));
    }

    public Admission getAdmissionNumberOrThrow(String admissionNumber) {
        return repository.findByAdmissionNo(admissionNumber)
                .orElseThrow(() -> APIException.notFound("Admission Number {0} Not Found", admissionNumber));
    }

    public Page<Admission> getAdmissions(String patientNo, String admissionNo, Admission.Status status, DateRange range, Pageable page) {

        return repository.findAll(page);
    }

    public Admission updateAdmission(Long id, AdmissionData data) {
        Patient patient = getPatientOrThrow(data.getPatientNumber());
        Bed bed = bedService.getBedOrThrow(data.getBedId());
        Employee medic = employeeRepository.findById(data.getAdmittingDoctorId()).orElse(null);

        Admission admission = getAdmissionOrThrow(id);
        admission.setAdmissionDate(data.getAdmissionDate());
        admission.setAdmissionReason(data.getAdmissionReason());
        admission.setBed(bed);
        admission.setDischargeDate(data.getDischargeDate());
        admission.setMedic(medic);
        admission.setPatient(patient);
        admission.setStatus(data.getStatus());
        admission.setType(data.getAdmissionType());
        return repository.save(admission);
    }
   @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateAdmissionStatus(String admissionNumber, Admission.Status status) {
        repository.updateAdmissionStatus(admissionNumber, status);
        Admission savedAdmission = getAdmissionNumberOrThrow(admissionNumber);
        Bed occupiedBed = savedAdmission.getBed();
        occupiedBed.setStatus(Bed.Status.Available);
        bedService.save(occupiedBed);
    }

    public void deleteAdmission(Long id) {
        Admission admission = getAdmissionOrThrow(id);
//        repository.delete(admission);
        repository.voidAdmission(admission.getAdmissionNo(), SecurityUtils.getCurrentUserLogin().orElse("System"));
    }

    public Patient getPatientOrThrow(String patientNumber) {
        return patientRepository.findByPatientNumber(patientNumber)
                .orElseThrow(() -> APIException.notFound("Patient Number {0} Not Found", patientNumber));
    }
}
