/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.service;

import io.smarthealth.clinical.record.domain.SickOffNote;
import io.smarthealth.clinical.record.domain.SickOffNoteRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.person.patient.domain.Patient;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author Simon.waweru
 */
@Service
public class SickOffNoteService {

    @Autowired
    SickOffNoteRepository sickOffNoteRepository;

    public SickOffNote createSickOff(SickOffNote note) {
        return sickOffNoteRepository.save(note);
    }

    public SickOffNote fetchSickNoteByVisitWithNotFoundThrow(final Visit visit) {
        return sickOffNoteRepository.findByVisit(visit).orElseThrow(() -> APIException.notFound("Sick off note identified by visit number {0} is not available ", visit.getVisitNumber()));
    }

    public Optional<SickOffNote> fetchSickNoteByVisit(final Visit visit) {
        return sickOffNoteRepository.findByVisit(visit);
    }

    public Optional<SickOffNote> fetchSickNoteByVisitId(final Long id) {
        return sickOffNoteRepository.findById(id);
    }

    public Page<SickOffNote> fetchSickOffNoteByPatient(final Patient patient, final Pageable pageable) {
        return sickOffNoteRepository.findByPatient(patient, pageable);
    }
}
