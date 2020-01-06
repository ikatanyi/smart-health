/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.service;

import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.visit.data.VisitData;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.waweru
 */
@Service
public class VisitService {

    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final ServicePointService servicePointService;

    public VisitService(VisitRepository visitRepository, PatientRepository patientRepository, ServicePointService servicePointService) {
        this.visitRepository = visitRepository;
        this.patientRepository = patientRepository;
        this.servicePointService = servicePointService;
    }

    public Page<Visit> fetchVisitByPatientNumber(String patientNumber, final Pageable pageable) {
        Patient patient = this.findPatientEntityOrThrow(patientNumber);
        Page<Visit> visits = visitRepository.findByPatient(patient, pageable);
        return visits;
    }

    public Page<Visit> fetchAllVisits(final Pageable pageable) {
        Page<Visit> visits = visitRepository.findAll(pageable);
        return visits;
    }

    @Transactional
    public Visit createAVisit(final Visit visit) {
        try {
            return visitRepository.save(visit);
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("There was an error creating visit", e.getMessage());
        }
    }

    public String updateVisit(final String visitNumber, final VisitData visitDTO) {
        findVisitEntityOrThrow(visitNumber);
        //validate and fetch patient
        Patient patient = findPatientEntityOrThrow(visitDTO.getPatientNumber());

        Visit visitEntity = VisitData.map(visitDTO);
        visitEntity.setPatient(patient);
        visitRepository.save(visitEntity);
        return visitDTO.getVisitNumber();
    }

//    public int generateVisitNumber() {
//        return visitRepository.maxVisitId() + 1;
//    }
    private Patient findPatientEntityOrThrow(String patientNumber) {
        return this.patientRepository.findByPatientNumber(patientNumber)
                .orElseThrow(() -> APIException.notFound("Patient Number {0} not found.", patientNumber));
    }

    public Page<Visit> findVisitByStatus(final VisitEnum.Status status, Pageable pageable) {
        return visitRepository.findByStatus(status, pageable);
    }

    public Page<Visit> findVisitByServicePoint(final ServicePoint servicePoint, Pageable pageable) {
        return visitRepository.findByServicePoint(servicePoint, pageable);
    }

    public Visit findVisitEntityOrThrow(String visitNumber) {
        return this.visitRepository.findByVisitNumber(visitNumber)
                .orElseThrow(() -> APIException.notFound("Visit Number {0} not found.", visitNumber));
    }

    public VisitData convertVisitEntityToData(Visit visit) {
        return VisitData.map(visit);
    }

}
