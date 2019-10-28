/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.service;

import io.smarthealth.clinical.record.domain.PatientDiagnosis;
import io.smarthealth.clinical.record.domain.PatientDiagnosisRepository;
import io.smarthealth.clinical.visit.data.AdmissionData;
import io.smarthealth.clinical.visit.domain.Admission;
import io.smarthealth.clinical.visit.domain.AdmissionRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.domain.EmployeeRepository;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
public class AdmissionService {

    @Autowired
    AdmissionRepository admissionRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    VisitRepository visitRepository;

    @Autowired
    PatientDiagnosisRepository diagnosisRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ModelMapper modelMapper;

    public Long createAdmission(AdmissionData admissionDTO) {

        Visit visit = visitRepository.findByVisitNumber(admissionDTO.getVisitNumber())
                .orElseThrow(() -> APIException.notFound("Visit with id {0} is not found", admissionDTO.getVisitNumber()));
        
        
        List<PatientDiagnosis> diagnosisProv = diagnosisRepository.findByVisit(visit);

        Optional<Employee> admittingDoctor = employeeRepository.findById(admissionDTO.getEmployeeId());
        Admission admission = convertAdmissionDataToAdmission(admissionDTO);
        if (!diagnosisProv.isEmpty()) {
            admission.setProvisionDiagnosis(diagnosisProv);
        }
        if (admittingDoctor.isPresent()) {
            admission.setAdmittingDoctor(admittingDoctor.get());
        }
        Admission adm = admissionRepository.save(admission);
        return adm.getId();
    }

    public AdmissionData fetchAdmissionHistoryByPatient(final String admissionNumber) {
        return null;
    }

    public String updateAdmissionDetails(String admissionNumber, AdmissionData admissionDTO) {
        return null;
    }

    public AdmissionData convertAdmissionToData(Admission admission) {
        AdmissionData admissionData = modelMapper.map(admission, AdmissionData.class);
        return admissionData;
    }

    public Admission convertAdmissionDataToAdmission(AdmissionData admissionData) {
        Admission admission = modelMapper.map(admissionData, Admission.class);
        return admission;
    }

}
