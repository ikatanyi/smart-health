/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.queue.service;

import io.smarthealth.administration.servicepoint.data.ServicePointData;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.queue.data.PatientQueueData;
import io.smarthealth.clinical.queue.domain.PatientQueue;
import io.smarthealth.clinical.queue.domain.PatientQueueRepository;
import io.smarthealth.clinical.queue.domain.specification.PatientQueueSpecification;
import io.smarthealth.clinical.visit.data.VisitData;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.DepartmentService;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PatientQueueService {
    
    @Autowired
    PatientQueueRepository patientQueueRepository;
    
    @Autowired
    DepartmentService departmentService;
    
    @Autowired
    PatientService patientService;
    
    @Autowired
    EmployeeService employeeService;
    
    @Autowired
    ServicePointService servicePointService;
    
    @Autowired
    VisitRepository visitRepository;
    
    @Transactional(rollbackFor = Exception.class)
    public PatientQueue createPatientQueue(PatientQueue patientQueue) {
        //if (!patientIsQueued(patientQueue.getDepartment(), patientQueue.getPatient())) {
        return patientQueueRepository.save(patientQueue);
//        } else {
//            throw APIException.conflict("Patient is already queued in the department selected", "");
//        }
    }
    
    public Page<PatientQueue> fetchQueueByDept(final ServicePoint servicePoint, final boolean status, Pageable pageable) {
        return patientQueueRepository.findByServicePointAndStatus(servicePoint, status, pageable);
    }
    
    public Page<PatientQueue> fetchQueueByPatient(Patient patient, Pageable pageable) {
        return patientQueueRepository.findByPatient(patient, pageable);
    }
    
    public PatientQueue fetchQueueByID(Long id) {
        return patientQueueRepository.findById(id).orElseThrow(() -> APIException.notFound("Queue identified by id {0} is not available", id));
    }

    public void Deactivate(ServicePoint servicePoint) {
        Page<PatientQueue> queueList = patientQueueRepository.findByServicePointAndStatus(servicePoint, true, Pageable.unpaged());
        for(PatientQueue queue : queueList){
            queue.setStatus(false);
            queue.setStopTime(Instant.now());
            patientQueueRepository.save(queue);
        }            
        return ;
    }
//    public Page<PatientQueue> fetchQueue(Pageable pageable) {
//        // return patientQueueRepository.findAll(pageable);
//        return patientQueueRepository.findActivePatientQueue(pageable);
//    }
    public Page<PatientQueue> fetchQueue(final String visitNumber, final String staffNumber, final String servicePointType, String patientNumber, final Pageable pageable) {
        Visit visit = null;
        Employee employee = null;
        ServicePoint servicePoint = null;
        Patient patient = null;
        if (visitNumber != null) {
            visit = visitRepository.findByVisitNumber(visitNumber).orElseThrow(() -> APIException.notFound("Visit identified by visit number {0} not found", visitNumber));
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
        Specification<PatientQueue> queueSpec = PatientQueueSpecification.createSpecification(visit, employee, servicePoint, patient);
        return patientQueueRepository.findAll(queueSpec, pageable);
        //return patientQueueRepository.findActivePatientQueue(pageable);
    }
    
    public List<PatientQueue> fetchQueueByVisit(Visit visit) {
        return patientQueueRepository.findByVisit(visit);
    }
    
    public boolean patientIsQueued(final ServicePoint servicePoint, final Patient patient) {
        System.out.println("Service point "+servicePoint.getName());
        System.out.println("Patient "+patient.getFullName());
        return patientQueueRepository.findByPatientAndServicePointAndStatus(patient, servicePoint, true).isPresent();
//        Optional<PatientQueue> patientQueue = patientQueueRepository.findByPatientAndDepartmentAndStatus(patient, department, true);
//        if (patientQueue.isPresent()) {
//            return true;
//        } else {
//            return false;
//        }
    }
    
    public PatientQueueData convertToPatientQueueData(PatientQueue patientQueue) {
        PatientQueueData patientQueueData = new PatientQueueData();
        patientQueueData.setVisitNumber(patientQueue.getVisit().getVisitNumber());
        patientQueueData.setVisitData(VisitData.map(patientQueue.getVisit()));
        patientQueueData.setPatientNumber(patientQueue.getPatient().getPatientNumber());
        
        if (patientQueue.getServicePoint() != null) {
            patientQueueData.setServicePointData(ServicePointData.map(patientQueue.getServicePoint()));
            patientQueueData.setServicePointName(patientQueue.getServicePoint().getServicePointType().name());
        }
        patientQueueData.setPatientData(patientService.convertToPatientData(patientQueue.getPatient()));
        patientQueueData.setId(patientQueue.getId());
        if (patientQueue.getUrgency() != null) {
            patientQueueData.setUrgency(patientQueue.getUrgency().name());
        }
        patientQueueData.setStartDateTime(patientQueue.getCreatedOn());
        patientQueueData.setSpecialNotes(patientQueue.getSpecialNotes());
        return patientQueueData;
    }
}
