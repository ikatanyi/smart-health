/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.service;

import io.smarthealth.clinical.record.domain.TriageNotes;
import io.smarthealth.clinical.record.domain.TriageNotesRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.exception.APIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
public class TriageNotesService {
    
    @Autowired
    TriageNotesRepository triageNotesRepository;
    
    public TriageNotes createNewTriageNotes(final TriageNotes triageNotes) {
        return triageNotesRepository.save(triageNotes);
    }
    
    public Page<TriageNotes> fethAllTriageNotesByVisit(final Visit visit, final Pageable pageable) {
        return triageNotesRepository.findByVisit(visit, pageable);
    }
    
    public TriageNotes fetchTriageNoteById(Long id) {
        return triageNotesRepository.findById(id).orElseThrow(() -> APIException.notFound("Triage note identified by {0} not found ", id));
    }
    
    public void removeTriageNote(final TriageNotes tn) {
        triageNotesRepository.delete(tn);
    }
}
