/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.service;

import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.queue.domain.PatientQueue;
import io.smarthealth.clinical.queue.service.PatientQueueService;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.clinical.record.data.DoctorRequestData.RequestType;
import io.smarthealth.clinical.record.data.DoctorRequestItem;
import io.smarthealth.clinical.record.data.PrescriptionData;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.domain.DoctorsRequestRepository;
import io.smarthealth.clinical.record.domain.Prescription;
import io.smarthealth.clinical.record.domain.PrescriptionRepository;
import io.smarthealth.clinical.record.domain.specification.DoctorRequestSpecification;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.lang.DateConverter;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
@RequiredArgsConstructor
public class DoctorRequestService implements DateConverter {

    // @Autowired
    private final DoctorsRequestRepository doctorRequestRepository;

    //@Autowired
    private final PatientQueueService patientQueueService;

    // @Autowired
    private final ModelMapper modelMapper;

    private final ServicePointService servicePointService;

    private final PrescriptionRepository prescriptionRepository;

    @Transactional
    public List<DoctorRequest> createRequest(List<DoctorRequest> docRequests) {
        List<DoctorRequest> docReqs = doctorRequestRepository.saveAll(docRequests);
        //Send patient to queue
        for (DoctorRequest docRequest : docReqs) {
            PatientQueue patientQueue = new PatientQueue();
//            Department department = departmentService.findByServicePointTypeAndloggedFacility(docRequest.getRequestType());      
            ServicePoint servicePoint = servicePointService.getServicePointByType(ServicePointType.valueOf(docRequest.getRequestType().name()));
            //check if patient is already queued
            if (patientQueueService.patientIsQueued(servicePoint, docRequest.getPatient())) {
                continue;
            }
            patientQueue.setServicePoint(servicePoint);
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
    public Page<DoctorRequest> findAllRequestsByVisitAndRequestType(final Visit visit, final RequestType requestType, Pageable pageable) {
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

    public Page<DoctorRequest> fetchAllPastDoctorRequests(final String visitNumber, final String patientNumber, final RequestType requestType, final FullFillerStatusType fulfillerStatus, final String groupBy, Pageable pageable) {
        Specification<DoctorRequest> spec = DoctorRequestSpecification.createSpecification(visitNumber, patientNumber, requestType, fulfillerStatus, groupBy);

        Page<DoctorRequest> docReqs = doctorRequestRepository.findAll(spec, pageable);
        return docReqs;
    }

    public Page<DoctorRequest> fetchAllDoctorRequests(final String visitNumber, final String patientNumber, final RequestType requestType, final FullFillerStatusType fulfillerStatus, final String groupBy, Pageable pageable) {
        Specification<DoctorRequest> spec = DoctorRequestSpecification.createSpecification(visitNumber, patientNumber, requestType, fulfillerStatus, groupBy);

        Page<DoctorRequest> docReqs = doctorRequestRepository.findAll(spec, pageable);
        return docReqs;
    }

//    public Page<DoctorRequest> fetchDoctorRequestLine(final String fulfillerStatus, final RequestType requestType, Pageable pageable) {
//        
//        return doctorRequestRepository.findRequestLine(fulfillerStatus, requestType, pageable);
//    }
//    
    public List<DoctorRequest> fetchServiceRequests(final Patient patient, final FullFillerStatusType fullfillerStatus, final RequestType requestType, final Visit visit) {
        Specification<DoctorRequest> spec = DoctorRequestSpecification.createSpecification(visit.getVisitNumber(), patient.getPatientNumber(), requestType, fullfillerStatus, null);
        Pageable wholePage = Pageable.unpaged();
        return doctorRequestRepository.findAll(spec, wholePage).getContent();
    }

    public List<DoctorRequest> fetchServiceRequestsByPatient(final Patient patient, final FullFillerStatusType fullfillerStatus, final RequestType requestType) {
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

    public DoctorRequestItem toData(DoctorRequest d) {
        DoctorRequestItem requestItem = new DoctorRequestItem();
        requestItem.setCode(d.getItem().getItemCode());
        requestItem.setItemId(d.getItem().getId());
        requestItem.setRate(d.getItemRate());
        requestItem.setCostRate(d.getItemCostRate());
        requestItem.setItemName(d.getItem().getItemName());
        requestItem.setRequestItemId(d.getId());
        if (d.getRequestType().equals(DoctorRequestData.RequestType.Pharmacy)) {
            requestItem.setPrescriptionData(PrescriptionData.map(prescriptionRepository.findPresriptionByRequestId(d.getId())));
        }
        requestItem.setOrderNo(d.getOrderNumber());
        requestItem.setRequestedByName(d.getRequestedBy().getUsername());
        requestItem.setStatus(d.getFulfillerStatus().name());

        return requestItem;
    }
}
