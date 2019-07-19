/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.documents.domain.service;

import io.smarthealth.clinical.documents.domain.PatientDiagnosis;
import io.smarthealth.clinical.documents.domain.PatientDiagnosisRepository;
import io.smarthealth.clinical.documents.domain.api.Diagnosis;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.utility.APIException;
import io.smarthealth.infrastructure.utility.ContentPage;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import java.util.ArrayList;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
public class DiagnosisService {

    @Autowired
    private PatientDiagnosisRepository diagnosisRepository;
    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private PatientRepository patientRepository;

    public Long addDiagnosis(String visitNumber, Diagnosis diagnosis) {

        Visit visit = findVisitOrThrow(visitNumber);

        if (!StringUtils.equalsIgnoreCase(visit.getPatient().getPatientNumber(), diagnosis.getPatientNumber())) {
            throw APIException.badRequest("Invalid Patient Number! mismatch in Patient Visit's patient number");
        }

        PatientDiagnosis diagnosisEntity = Diagnosis.map(diagnosis);
        diagnosisEntity.setVisit(visit);
        diagnosisEntity.setPatient(visit.getPatient());
        return diagnosisRepository.save(diagnosisEntity).getId();
    }

    public ContentPage<Diagnosis> fetchDiagnosisByVisit(String visitNumber, Pageable page) {
        final ContentPage<Diagnosis> triagePage = new ContentPage();
        Optional<Visit> visit = visitRepository.findByVisitNumber(visitNumber);
        if (visit.isPresent()) {
            Page<PatientDiagnosis> triageEntities = this.diagnosisRepository.findByPatient(visit.get().getPatient(), page);

            triagePage.setTotalPages(triageEntities.getTotalPages());
            triagePage.setTotalElements(triageEntities.getTotalElements());
            if (triageEntities.getSize() > 0) {
                final ArrayList<Diagnosis> triagelist = new ArrayList<>(triageEntities.getSize());
                triagePage.setContents(triagelist);
                triageEntities.forEach((vt) -> triagelist.add(Diagnosis.map(vt)));
            }
        }
        return triagePage;
    }

    public Diagnosis getDiagnosisById(String visitNumber, Long id) {

        Optional<PatientDiagnosis> entity = diagnosisRepository.findById(id);

        return entity.map(Diagnosis::map).orElse(null);
    }

    public ContentPage<Diagnosis> fetchDiagnosis(String patientNumber, Pageable page) {

        Page<PatientDiagnosis> diagnosisEntities = Page.empty();
        if (patientNumber != null) {
            Optional<Patient> patient = patientRepository.findByPatientNumber(patientNumber);
            if (patient.isPresent()) {
                diagnosisEntities = diagnosisRepository.findByPatient(patient.get(), page);
            }
        } else {
            diagnosisEntities = diagnosisRepository.findAll(page);
        }

        final ContentPage<Diagnosis> triagePage = new ContentPage();
        triagePage.setTotalPages(diagnosisEntities.getTotalPages());
        triagePage.setTotalElements(diagnosisEntities.getTotalElements());
        if (diagnosisEntities.getSize() > 0) {
            final ArrayList<Diagnosis> triagelist = new ArrayList<>(diagnosisEntities.getSize());
            triagePage.setContents(triagelist);
            diagnosisEntities.forEach((vt) -> triagelist.add(Diagnosis.map(vt)));
        }

        return triagePage;

    }

    private Visit findVisitOrThrow(String visitNumber) {
        return this.visitRepository.findByVisitNumber(visitNumber)
                .orElseThrow(() -> APIException.notFound("Patient Session with Visit Number : {0} not found.", visitNumber));
    }

}
