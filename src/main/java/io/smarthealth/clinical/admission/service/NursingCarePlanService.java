/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.service;

import io.smarthealth.clinical.admission.data.NursingCarePlanData;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.domain.NursingCarePlan;
import io.smarthealth.clinical.admission.domain.repository.NursingCarePlanRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class NursingCarePlanService {
    
    private final NursingCarePlanRepository nursingCarePlanRepository;
    private final AdmissionService admissionService;

    //create
    public NursingCarePlan createNursingCarePlan(NursingCarePlanData d) {
        //fetch admission by number
        Admission a = admissionService.findAdmissionByNumber(d.getAdmissionNumber());
        
        NursingCarePlan cp = NursingCarePlanData.map(d);
        cp.setAdmission(a);
        cp.setPatient(a.getPatient());
        
        return nursingCarePlanRepository.save(cp);
    }

    //read
    public List<NursingCarePlan> fetchNursingCarePlanByAdmissionNumber(final String admissionNumber) {
        Admission a = admissionService.findAdmissionByNumber(admissionNumber);
        return nursingCarePlanRepository.findByAdmission(a);
    }

    //read 
    public NursingCarePlan fetchNursingCarePlanById(final Long id) {
        return nursingCarePlanRepository.findById(id).orElseThrow(() -> APIException.notFound("Nursing care plan identified by {0} not found", id));
    }

    //update 
    public NursingCarePlan updateNursingCarePlan(final Long id, final NursingCarePlanData d) {
        NursingCarePlan e = fetchNursingCarePlanById(id);
        e.setDatetime(d.getDateTime());
        e.setDiagnosis(d.getDiagnosis());
        e.setDoneBy(d.getDoneBy());
        e.setEvaluation(d.getEvaluation());
        e.setExpectedOutcome(d.getExpectedOutcome());
        e.setIntervention(d.getIntervention());
        e.setPlanOfCare(d.getPlanOfCare());

        //fetch admission by number
        Admission a = admissionService.findAdmissionByNumber(d.getAdmissionNumber());
        
        e.setAdmission(a);
        e.setPatient(a.getPatient());
        
        return nursingCarePlanRepository.save(e);
    }

    //Delete
    public void removeNursingCarePlan(final Long id) {
        NursingCarePlan e = fetchNursingCarePlanById(id);
        nursingCarePlanRepository.delete(e);
    }
    
}
