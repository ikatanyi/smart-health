/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.service;

import io.smarthealth.clinical.admission.data.CareTeamData;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.domain.CareTeam;
import io.smarthealth.clinical.admission.domain.repository.CareTeamRepository;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

}
