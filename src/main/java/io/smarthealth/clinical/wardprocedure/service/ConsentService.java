/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.wardprocedure.service;

import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.clinical.wardprocedure.data.ConsentFormData;
import io.smarthealth.clinical.wardprocedure.domain.ConsentForm;
import io.smarthealth.clinical.wardprocedure.domain.repository.ConsentFormRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentFormRepository consentFormRepository;
    private final VisitService visitService;

    public ConsentForm saveConsentForm(ConsentFormData consentFormData) {
        Visit visit = visitService.findVisitEntityOrThrow(consentFormData.getVisitNumber());
        ConsentForm c = consentFormData.fromData();
        c.setVisit(visit);
        return consentFormRepository.save(c);
    }

    public List<ConsentForm> fetchConsentFormsByVisit(final String visitNumber) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);

        return consentFormRepository.findByVisit(visit);
    }

    public List<ConsentForm> fetchConsentFormsByVisitAndType(final String visitNumber, final String type) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);

        return consentFormRepository.findByVisitAndConsentType(visit, type);
    }
}
