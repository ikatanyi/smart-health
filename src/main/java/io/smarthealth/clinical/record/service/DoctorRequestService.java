/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.service;

import io.smarthealth.clinical.queue.domain.PatientQueue;
import io.smarthealth.clinical.queue.service.PatientQueueService;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.domain.DoctorsRequestRepository;
import io.smarthealth.clinical.record.domain.specification.DoctorRequestSpecification;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.lang.DateConverter;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.service.DepartmentService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.specification.ItemSpecification;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
public class DoctorRequestService implements DateConverter {

    // @Autowired
    private final DoctorsRequestRepository doctorRequestRepository;

    //@Autowired
    private final PatientQueueService patientQueueService;

    // @Autowired
    private final ModelMapper modelMapper;

    private final DepartmentService departmentService;

    public DoctorRequestService(DoctorsRequestRepository doctorRequestRepository, PatientQueueService patientQueueService, ModelMapper modelMapper, DepartmentService departmentService) {
        this.doctorRequestRepository = doctorRequestRepository;
        this.patientQueueService = patientQueueService;
        this.modelMapper = modelMapper;
        this.departmentService = departmentService;
    }

    @Transactional
    public List<DoctorRequest> createRequest(List<DoctorRequest> docRequests) {
        List<DoctorRequest> docReqs = doctorRequestRepository.saveAll(docRequests);
        //Send patient to queue
        for (DoctorRequest docRequest : docReqs) {
            PatientQueue patientQueue = new PatientQueue();
            Department department = departmentService.findByServicePointTypeAndloggedFacility(docRequest.getRequestType());             //check if patient is already queued
            if (patientQueueService.patientIsQueued(department, docRequest.getPatient())) {
                continue;
            }
            patientQueue.setDepartment(department);
            patientQueue.setPatient(docRequest.getPatient());
            patientQueue.setSpecialNotes("");
            patientQueue.setStatus(true);
            patientQueue.setUrgency(PatientQueue.QueueUrgency.Medium);
            patientQueue.setVisit(docRequest.getVisit());
            PatientQueue savedQueue = patientQueueService.createPatientQueue(patientQueue);
        }

        return docReqs;

    }

//    public List<DoctorRequestData> findAll(final String visitNumber, final String status, final String requestType, String from, String to, Pageable page) {
//        Date from1 = new Date();
//        Date to1 = new Date();
//        Specification<DoctorRequest> spec = DoctorRequestSpecification.createSpecification(visitNumber, status, requestType, from1, to1);
//        List<DoctorRequest> docReqs = doctorRequestRepository.findAll(spec, page).getContent();
//        List<DoctorRequestData> docReqData = modelMapper.map(docReqs, new TypeToken<List<DoctorRequestData>>() {
//        }.getType());
//        return docReqData;
//    }
    public Page<DoctorRequest> findAllRequestsByVisitAndRequestType(final Visit visit, final String requestType, Pageable pageable) {
        Page<DoctorRequest> docReqs = doctorRequestRepository.findByVisitAndRequestType(visit, requestType, pageable);
        return docReqs;
    }

    public Page<DoctorRequest> findAllRequestsByVisit(final Visit visit, Pageable pageable) {
        Page<DoctorRequest> docReqs = doctorRequestRepository.findByVisit(visit, pageable);
        return docReqs;
    }

    public Page<DoctorRequest> findAllRequestsByOrderNoAndRequestType(final String orderNo, String requestType, Pageable pageable) {
        Page<DoctorRequest> docReqs = doctorRequestRepository.findByOrderNumberAndRequestType(orderNo, requestType, pageable);
        return docReqs;
    }

    public Page<DoctorRequest> fetchAllDoctorRequests(final String visitNumber, final String requestType, final String fulfillerStatus, Pageable pageable) {
        Specification<DoctorRequest> spec = DoctorRequestSpecification.createSpecification(visitNumber, requestType, fulfillerStatus);

        Page<DoctorRequest> docReqs = doctorRequestRepository.findAll(spec, pageable);
        return docReqs;
    }

    public Page<DoctorRequest> fetchDoctorRequestLine(final String fulfillerStatus, final String requestType, Pageable pageable) {
        return doctorRequestRepository.findRequestLine(fulfillerStatus, requestType, pageable);
    }

    public List<DoctorRequest> fetchServiceRequestsByPatient(final Patient patient, final String fullfillerStatus, final String requestType) {
        return doctorRequestRepository.findServiceRequestsByPatient(patient, fullfillerStatus, requestType);
    }

    public Optional<DoctorRequestData> getDocRequestById(Long id) {
        Optional<DoctorRequestData> entity = doctorRequestRepository.findById(id).map(p -> DoctorRequestToData(p));
        return entity;
    }

    public DoctorRequestData UpdateDocRequest(DoctorRequestData requestData) {
        DoctorRequest docReq = convertDoctorRequestData(requestData);
        Optional<DoctorRequest> entity = doctorRequestRepository.findById(docReq.getId());
        if (entity.isPresent()) {
            docReq = doctorRequestRepository.save(entity.get());
        }
        return DoctorRequestToData(docReq);
    }

    public ResponseEntity<?> deleteById(long Id) {
        try {
            doctorRequestRepository.deleteById(Id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public DoctorRequestData DoctorRequestToData(DoctorRequest docRequest) {
        DoctorRequestData docReqData = modelMapper.map(docRequest, DoctorRequestData.class);
        return docReqData;
    }

    public DoctorRequest convertDoctorRequestData(DoctorRequestData docRequestData) {
        DoctorRequest docReqData = modelMapper.map(docRequestData, DoctorRequest.class);
        return docReqData;
    }
}
