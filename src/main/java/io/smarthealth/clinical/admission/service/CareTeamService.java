/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.service;

import io.smarthealth.clinical.admission.data.CareTeamData;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.domain.BedType;
import io.smarthealth.clinical.admission.domain.CareTeam;
import io.smarthealth.clinical.admission.domain.CareTeamRole;
import io.smarthealth.clinical.admission.domain.repository.CareTeamRepository;
import io.smarthealth.clinical.admission.domain.specification.AdmissionSpecification;
import io.smarthealth.clinical.admission.domain.specification.CareTeamSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class CareTeamService {

    private final CareTeamRepository careTeamRepository;
    private final EmployeeService employeeService;
    private final AdmissionService admissionService;

    public List<CareTeam> createCareTeam(List<CareTeamData> data) {
        List<CareTeam> ct = new ArrayList<>();
        for (CareTeamData d : data) {
            //find med selected
            Employee med = employeeService.findEmployeeById(d.getMedicId());
            //find Admission
            Admission adm = admissionService.findAdmissionByNumber(d.getAdmissionNumber());

            CareTeam e = CareTeamData.map(d);
            e.setIsActive(Boolean.TRUE);
            e.setAdmission(adm);
            e.setMedic(med);
            e.setPatient(adm.getPatient());
            ct.add(e);
        }
        return careTeamRepository.saveAll(ct);
    }

    public List<CareTeam> fetchCareTeamByAdmissionNumber(final String admissionNumber) {
        Admission adm = admissionService.findAdmissionByNumber(admissionNumber);
        return careTeamRepository.findByAdmission(adm);
    }

    public CareTeam fetchCareTeamByAdmissionNumberAndCareRole(final String admissionNumber, final CareTeamRole role) {
        return careTeamRepository.findCareTeamByAdmission_AdmissionNoAndCareRole(admissionNumber, role)
                .orElse(null);
    }

    public CareTeam getCareTeam(Long id) {
        return careTeamRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("CareTeam with id  {0} not found.", id));
    }
    
    public Page<CareTeam> getCareTeams(String patientNo, String admissionNo, CareTeamRole careRole, Boolean active,  Boolean voided, Pageable page) {
        Specification<CareTeam> s = CareTeamSpecification.createSpecification(patientNo, admissionNo, careRole, active,  voided);
        return careTeamRepository.findAll(s,page);
    }
    
    public CareTeam removeCareTeam(Long id, String reason) {
        CareTeam ct = getCareTeam(id);
        ct.setIsActive(Boolean.FALSE);
        ct.setVoided(Boolean.TRUE);
        ct.setReason(reason);
        return careTeamRepository.save(ct);
    }
    
    public List<CareTeam> addCareTeam(List<CareTeamData> ctdata){
        List<CareTeam> ctList = ctdata.stream().map(c
                -> {
            CareTeam ct = CareTeamData.map(c);
            Admission adm = admissionService.findAdmissionByNumber(c.getAdmissionNumber());
            ct.setIsActive(Boolean.TRUE);
            ct.setAdmission(adm);
            ct.setPatient(adm.getPatient());
            ct.setMedic(employeeService.findEmployeeById(c.getMedicId()));
            return ct;
        }
        ).collect(Collectors.toList());
       return careTeamRepository.saveAll(ctList);
    }

    

    public CareTeam updateCareTeam(Long id, CareTeamData data) {
        CareTeam ct = getCareTeam(id);
        //find med selected
        Employee med = employeeService.findEmployeeById(data.getMedicId());
        //find Admission
        Admission adm = admissionService.findAdmissionByNumber(data.getAdmissionNumber());
        ct.setAdmission(adm);
        ct.setCareRole(data.getRole());
        ct.setDateAssigned(data.getDateAssigned());
        ct.setIsActive(Boolean.TRUE);
        ct.setMedic(med);
        ct.setPatient(adm.getPatient());
        return careTeamRepository.save(ct);
    }

}
