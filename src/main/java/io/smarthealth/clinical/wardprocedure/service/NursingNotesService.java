/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.wardprocedure.service;

import io.smarthealth.clinical.wardprocedure.data.NursingNotesData;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.wardprocedure.domain.NursingNotes;
import io.smarthealth.clinical.wardprocedure.domain.repository.NursingNotesRepository;
import io.smarthealth.clinical.admission.service.AdmissionService;
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
public class NursingNotesService {
    
    private final NursingNotesRepository nursingNotesRepository;
    private final AdmissionService admissionService;

    //create
    public NursingNotes createNursingNotes(NursingNotesData d) {
        //fetch admission by number
        Admission a = admissionService.findAdmissionByNumber(d.getAdmissionNumber());
        
        NursingNotes e = NursingNotesData.map(d);
        e.setAdmission(a);
        e.setPatient(a.getPatient());
        
        return nursingNotesRepository.save(e);
    }

    //read by admission number
    public List<NursingNotes> fetchNursingNotesByAdmissionNumber(final String admissionNumber) {

        //fetch admission by number
        Admission a = admissionService.findAdmissionByNumber(admissionNumber);
        
        return nursingNotesRepository.findByAdmission(a);
    }

    //read by id
    public NursingNotes fetchNursingNoteById(final Long id) {
        return nursingNotesRepository.findById(id).orElseThrow(() -> APIException.notFound("Nursing note identified by {0} not found", id));
    }

    //update 
    public NursingNotes updateNursingNote(final Long id, final NursingNotesData d) {
        NursingNotes e = fetchNursingNoteById(id);
        e.setDatetime(d.getDatetime());
        e.setNotes(d.getNotes());
        e.setNotesBy(d.getNotesBy());
        e.setStatus(d.getStatus());
        
        Admission a = admissionService.findAdmissionByNumber(d.getAdmissionNumber());
        
        e.setAdmission(a);
        e.setPatient(a.getPatient());
        
        return nursingNotesRepository.save(e);
    }

    //delete
    public void removeNursingNote(final Long id) {
        nursingNotesRepository.delete(fetchNursingNoteById(id));
    }
    
}
