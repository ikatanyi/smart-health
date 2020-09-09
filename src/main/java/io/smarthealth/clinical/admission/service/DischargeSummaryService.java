/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.service;

import io.smarthealth.clinical.admission.data.DischargeSummaryData;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.domain.Bed;
import io.smarthealth.clinical.admission.domain.DischargeSummary;
import io.smarthealth.clinical.admission.domain.repository.BedRepository;
import io.smarthealth.clinical.admission.domain.repository.DischargeSummaryRepository;
import io.smarthealth.clinical.admission.domain.specification.DischargeSummarySpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Service
@RequiredArgsConstructor
public class DischargeSummaryService {

    private final SequenceNumberService sequenceNumberService;
    private final AdmissionService admissionService;
    private final EmployeeService employeeService;
    private final DischargeSummaryRepository dischargeSummaryRepository;
    private final BedRepository bedRepository;

    public DischargeSummary createDischargeSummary(DischargeSummaryData data) {
        DischargeSummary discharge = data.map();
        Admission admission = admissionService.findAdmissionById(data.getAdmissionId());   
        Employee doctor = employeeService.findEmployeeById(data.getDoctorId());
        discharge.setAdmission(admission);
        discharge.setDoctor(doctor);
        discharge.setPatient(admission.getPatient());
        Bed AvailBed=admission.getBed(); 
        AvailBed.setStatus(Bed.Status.Available);
        bedRepository.save(AvailBed);
       
        discharge.setDischargeNo(sequenceNumberService.next(1l, Sequences.DischargeNumber.name()));
        return dischargeSummaryRepository.save(discharge);
    }

    public Page<DischargeSummary> fetchDischargeSummarys(final String dischargeNo, Long doctorId, Long patientId, String term, DateRange range, final Pageable pageable) {
        Specification<DischargeSummary> s = DischargeSummarySpecification.createSpecification(dischargeNo, doctorId,patientId, term, range);
        return dischargeSummaryRepository.findAll(s, pageable);
    }

    public DischargeSummary findDischargeSummaryById(Long id) {
        if (id != null) {
            return dischargeSummaryRepository.findById(id).orElseThrow(() -> APIException.notFound("DischargeSummary id {0} not found", id));
        } else {
            throw APIException.badRequest("Please provide dischrage id ", "");
        }
    }

    public DischargeSummary findDischargeSummaryByNumber(String admissionNo) {
        if (admissionNo != null) {
            return dischargeSummaryRepository.findByDischargeNo(admissionNo).orElseThrow(() -> APIException.notFound("DischargeSummary with number {0} not found", admissionNo));
        } else {
            throw APIException.badRequest("Please provide admission number ", "");
        }
    }
    
    public DischargeSummary updateDischargeSummary(Long id, DischargeSummaryData data){
        DischargeSummary summary = findDischargeSummaryById(id);
        Admission admission = admissionService.findAdmissionById(data.getAdmissionId());
        Employee doctor = employeeService.findEmployeeById(data.getDoctorId());
        summary.setAdmission(admission);
        summary.setDoctor(doctor);
        summary.setPatient(admission.getPatient());
        
        summary.setClinicalSummary(data.getClinicalSummary());
        summary.setDiagnosis(data.getDiagnosis());
        summary.setDischargeDate(data.getDischargeDate());
        summary.setDischargeMethod(data.getDischargeMethod());
        summary.setInstructions(data.getInstructions());
        summary.setInvestigations(data.getInvestigations());
        summary.setManagement(data.getManagement());
        summary.setOtherIllness(data.getOtherIllness());
        summary.setRecommendations(data.getRecommendations());
        summary.setRequestedBy(data.getRequestedBy());
        Bed AvailBed=admission.getBed(); 
        AvailBed.setStatus(Bed.Status.Available);
        bedRepository.save(AvailBed);
        
        return dischargeSummaryRepository.save(summary);
    }
}
