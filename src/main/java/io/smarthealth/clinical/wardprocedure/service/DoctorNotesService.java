/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.wardprocedure.service;

import io.smarthealth.clinical.wardprocedure.data.DoctorNotesData;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.wardprocedure.domain.DoctorNotes;
import io.smarthealth.clinical.wardprocedure.domain.repository.DoctorNotesRepository;
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
public class DoctorNotesService {

    private final DoctorNotesRepository doctorNotesRepository;
    private final AdmissionService admissionService;

    //create
    public DoctorNotes createDoctorNotes(DoctorNotesData d) {
        //fetch admission by number
        Admission a = admissionService.findAdmissionByNumber(d.getAdmissionNumber());

        DoctorNotes e = DoctorNotesData.map(d);
        e.setAdmission(a);
        e.setPatient(a.getPatient());

        return doctorNotesRepository.save(e);
    }

    //read by admission number
    public List<DoctorNotes> fetchDoctorNotesByAdmissionNumber(final String admissionNumber) {

        //fetch admission by number
        Admission a = admissionService.findAdmissionByNumber(admissionNumber);

        return doctorNotesRepository.findByAdmission(a);
    }

    //read by id
    public DoctorNotes fetchDoctorNoteById(final Long id) {
        return doctorNotesRepository.findById(id).orElseThrow(() -> APIException.notFound("Doctor note identified by {0} not found", id));
    }

    //update 
    public DoctorNotes updateDoctorNote(final Long id, final DoctorNotesData d) {
        DoctorNotes e = fetchDoctorNoteById(id);
        e.setDatetime(d.getDatetime());
        e.setNotes(d.getNotes());
        e.setNotesBy(d.getNotesBy());
        e.setStatus(d.getStatus());

        Admission a = admissionService.findAdmissionByNumber(d.getAdmissionNumber());

        e.setAdmission(a);
        e.setPatient(a.getPatient());

        return doctorNotesRepository.save(e);
    }

    //delete
    public void removeDoctorNote(final Long id) {
        doctorNotesRepository.delete(fetchDoctorNoteById(id));
    }

}
