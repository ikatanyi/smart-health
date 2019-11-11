/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.service;

import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.domain.DoctorsRequestRepository;
import io.smarthealth.clinical.record.domain.specification.DoctorRequestSpecification;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.lang.DateConverter;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private final DoctorsRequestRepository doctorRequestRepository;
    

    @Autowired
    ModelMapper modelMapper;

    public DoctorRequestService(DoctorsRequestRepository doctorRequestRepository, VisitRepository visitRepository, PatientRepository patientRepository
    ) {
        this.doctorRequestRepository = doctorRequestRepository;
    }

    public List<DoctorRequestData> createRequest(List<DoctorRequest> docRequests) {
        List<DoctorRequest> docReqs = doctorRequestRepository.saveAll(docRequests);
        return modelMapper.map(docReqs, new TypeToken<List<DoctorRequest>>() {
        }.getType());
    }

    public List<DoctorRequestData> findAll(final String visitNumber, final String status, final String requestType, String from, String to, Pageable page) {
        Date from1 = new Date();
        Date to1 = new Date();
        Specification<DoctorRequest> spec = DoctorRequestSpecification.createSpecification(visitNumber, status, requestType, from1, to1);
        List<DoctorRequest> docReqs = doctorRequestRepository.findAll(spec, page).getContent();
        List<DoctorRequestData> docReqData = modelMapper.map(docReqs, new TypeToken<List<DoctorRequestData>>() {
        }.getType());
        return docReqData;
    }

    public Page<DoctorRequest> findAllByVisit(final Visit visit, final String requestType, Pageable pageable) {
        Page<DoctorRequest> docReqs = doctorRequestRepository.findByVisitAndRequestType(visit, requestType, pageable);
        return docReqs;
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
