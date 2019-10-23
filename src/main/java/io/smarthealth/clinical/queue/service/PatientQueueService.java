/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.queue.service;

import io.smarthealth.clinical.queue.data.PatientQueueData;
import io.smarthealth.clinical.queue.domain.PatientQueue;
import io.smarthealth.clinical.queue.domain.PatientQueueRepository;
import io.smarthealth.clinical.visit.data.VisitData;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.service.DepartmentService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    @Transactional
    public PatientQueue createPatientQueue(PatientQueue patientQueue) {
        return patientQueueRepository.save(patientQueue);
    }
    
    public Page<PatientQueue> fetchQueueByDept(Department department, Pageable pageable) {
        return patientQueueRepository.findByDepartment(department, pageable);
    }
    
    public Page<PatientQueue> fetchQueueByPatient(Patient patient, Pageable pageable) {
        return patientQueueRepository.findByPatient(patient, pageable);
    }
    
    public PatientQueue fetchQueueByVisitNumber(Visit visit) {
        return patientQueueRepository.findByVisit(visit).orElseThrow(() -> APIException.notFound("Queue identified by visit number {0} is not available", visit.getVisitNumber()));
    }
    
    public Page<PatientQueue> fetchQueue(Pageable pageable) {
        return patientQueueRepository.findAll(pageable);
    }
    
    public PatientQueueData convertToPatientQueueData(PatientQueue patientQueue) {
        PatientQueueData patientQueueData = new PatientQueueData();
        patientQueueData.setVisitNumber(patientQueue.getVisit().getVisitNumber());
        patientQueueData.setVisitData(VisitData.map(patientQueue.getVisit()));
        patientQueueData.setPatientNumber(patientQueue.getPatient().getPatientNumber());
        patientQueueData.setDepartmentId(patientQueue.getDepartment().getId().toString());
        patientQueueData.setDepartmentData(departmentService.convertDepartmentToData(patientQueue.getDepartment()));
        patientQueueData.setPatientData(patientService.convertToPatientData(patientQueue.getPatient()));
        patientQueueData.setId(patientQueue.getId());
        if (patientQueue.getUrgency() != null) {
            patientQueueData.setUrgency(patientQueue.getUrgency().name());
        }
        patientQueueData.setSpecialNotes(patientQueue.getSpecialNotes());
        return patientQueueData;
    }
}
