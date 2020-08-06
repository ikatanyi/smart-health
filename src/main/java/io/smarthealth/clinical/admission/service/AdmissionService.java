/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.service;

import io.smarthealth.clinical.admission.data.AdmissionData;
import io.smarthealth.clinical.admission.data.CareTeamData;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.domain.Bed;
import io.smarthealth.clinical.admission.domain.BedType;
import io.smarthealth.clinical.admission.domain.CareTeam;
import io.smarthealth.clinical.admission.domain.Room;
import io.smarthealth.clinical.admission.domain.Ward;
import io.smarthealth.clinical.admission.domain.repository.AdmissionRepository;
import io.smarthealth.clinical.admission.domain.specification.AdmissionSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class AdmissionService {
    
    private final AdmissionRepository admissionRepository;
    private final SequenceNumberService sequenceNumberService;
    private final WardService wardService;
    private final BedService bedService;
    private final RoomService roomService;
    private final PatientService patientService;
    private final EmployeeService employeeService;
    
    @Transactional
    public Admission createAdmission(AdmissionData d) {
        Ward w = wardService.getWard(d.getWardId());
        Bed b = bedService.getBed(d.getBedId());
        BedType bt = bedService.getBedType(d.getBedTypeId());
        Patient p = patientService.findPatientOrThrow(d.getPatientNumber());
        
        Admission a = AdmissionData.map(d);
        a.setAdmissionNo(sequenceNumberService.next(1l, Sequences.Admission.name()));
        a.setWard(w);
        a.setBed(b);
        a.setBedType(bt);
        a.setPatient(p);
        if (d.getRoomId() != null) {
            Room room = roomService.getRoom(d.getRoomId());
            a.setRoom(room);
        }
        
        List<CareTeam> ctList = d.getCareTeam().stream().map(c
                -> {
            CareTeam ct = CareTeamData.map(c);
            ct.setAdmission(a);
            ct.setMedic(employeeService.findEmployeeById(c.getMedicId()));
            ct.setPatient(p);
            return ct;
        }
        ).collect(Collectors.toList());
        
        a.setCareTeam(ctList);
        
        return admissionRepository.save(a);
    }
    
    public Page<Admission> fetchAdmissions(final String admissionNo, final String term, final Pageable pageable) {
        Specification<Admission> s = AdmissionSpecification.createSpecification(admissionNo, term);
        return admissionRepository.findAll(s, pageable);
    }
    
    public Admission findAdmissionById(Long id) {
        if (id != null) {
            return admissionRepository.findById(id).orElseThrow(() -> APIException.notFound("Admission id {0} not found", id));
        } else {
            throw APIException.badRequest("Please provide admission id ", "");
        }
    }
    
    public Admission findAdmissionByNumber(String admissionNo) {
        if (admissionNo != null) {
            return admissionRepository.findByAdmissionNo(admissionNo).orElseThrow(() -> APIException.notFound("Admission with number {0} not found", admissionNo));
        } else {
            throw APIException.badRequest("Please provide admission number ", "");
        }
    }
}
