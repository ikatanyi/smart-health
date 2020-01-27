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
import io.smarthealth.clinical.visit.domain.specification.VisitSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.waweru
 */
@Service
public class VisitService {

    private final VisitRepository visitRepository;
    private final ServicePointService servicePointService;
    private final PatientService patientService;
    private final EmployeeService employeeService;

    public VisitService(VisitRepository visitRepository, ServicePointService servicePointService, PatientService patientService, EmployeeService employeeService) {
        this.visitRepository = visitRepository;
        this.servicePointService = servicePointService;
        this.patientService = patientService;
        this.employeeService = employeeService;
    }

    public Page<Visit> fetchVisitByPatientNumber(String patientNumber, final Pageable pageable) {
        Patient patient = this.findPatientEntityOrThrow(patientNumber);
        Page<Visit> visits = visitRepository.findByPatient(patient, pageable);
        return visits;
    }

    public Page<Visit> fetchAllVisits(final String visitNumber, final String staffNumber, final String servicePointType, String patientNumber, boolean runningStatus, DateRange range, final Pageable pageable) {
        Visit visit = null;
        Employee employee = null;
        ServicePoint servicePoint = null;
        Patient patient = null;
        if (visitNumber != null) {
            visit = this.findVisitEntityOrThrow(visitNumber);
        }
        if (staffNumber != null) {
            employee = employeeService.fetchEmployeeByNumberOrThrow(staffNumber);
        }
        if (servicePointType != null) {
            servicePoint = servicePointService.getServicePointByType(ServicePointType.valueOf(servicePointType));
        }
        if (patientNumber != null) {
            patient = patientService.findPatientOrThrow(patientNumber);
        }

//        System.out.println(" LocalDate.now().atStartOfDay() " + LocalDate.now().atStartOfDay());
        Specification<Visit> visitSpecs = VisitSpecification.createSpecification(visit, employee, servicePoint, patient, runningStatus,range);
        Page<Visit> visits = visitRepository.findAll(visitSpecs, pageable);
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

    public boolean isPatientVisitActive(Patient patient) {
        return visitRepository.isPatientVisitActive(patient);
    }

    private Patient findPatientEntityOrThrow(String patientNumber) {
        return this.patientService.findPatientOrThrow(patientNumber);
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
