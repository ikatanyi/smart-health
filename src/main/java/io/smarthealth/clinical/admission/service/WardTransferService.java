/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.service;

import io.smarthealth.clinical.admission.data.WardTransferData;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.domain.Bed;
import io.smarthealth.clinical.admission.domain.BedType;
import io.smarthealth.clinical.admission.domain.Room;
import io.smarthealth.clinical.admission.domain.Ward;
import io.smarthealth.clinical.admission.domain.WardTransfer;
import io.smarthealth.clinical.admission.domain.repository.AdmissionRepository;
import io.smarthealth.clinical.admission.domain.repository.BedRepository;
import io.smarthealth.clinical.admission.domain.repository.WardTransferRepository;
import io.smarthealth.clinical.admission.domain.specification.WardTransferSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.sequence.SequenceNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Service
@RequiredArgsConstructor
public class WardTransferService {

    private final SequenceNumberService sequenceNumberService;
    private final AdmissionService admissionService;
    private final AdmissionRepository admissionRepository;
    private final PatientService patientService;
    private final WardTransferRepository wardTransferRepository;
    private final RoomService roomService;
    private final WardService wardService;
    private final BedService bedService;
    private final BedRepository bedRepository;
    private final BedTypeService bedTypeService;

    public WardTransfer createWardTransfer(WardTransferData data) {
        WardTransfer transfer = data.map();
        Admission admission = admissionService.findAdmissionById(data.getAdmissionId());   
        Bed AvailBed = admission.getBed();
        
        Bed bed = bedService.getBed(data.getBedId());
        Room room = roomService.getRoom(bed.getRoom().getId());
        Ward ward = wardService.getWard(bed.getRoom().getWard().getId());
        BedType type = bedTypeService.getBedType(data.getBedTypeId());
        transfer.setAdmission(admission);
        transfer.setBed(bed);
        transfer.setWard(ward);
        transfer.setRoom(room);
        transfer.setBedType(type);
        transfer.setPatient(admission.getPatient());
        AvailBed.setStatus(Bed.Status.Available);
        bed.setStatus(Bed.Status.Occupied);        
        admission.setBed(bed);
        
        bedRepository.save(AvailBed);
        admissionRepository.save(admission);
        return wardTransferRepository.save(transfer);
    }

    public Page<WardTransfer> fetchWardTransfers(final Long wardId, Long roomId, Long bedId, Long patientId, DateRange range, final String term, final Pageable pageable) {
        Specification<WardTransfer> s = WardTransferSpecification.createSpecification(wardId, roomId, bedId,patientId, range, term);
        return wardTransferRepository.findAll(s, pageable);
    }

    public WardTransfer findWardTransferById(Long id) {
        if (id != null) {
            return wardTransferRepository.findById(id).orElseThrow(() -> APIException.notFound("WardTransfer id {0} not found", id));
        } else {
            throw APIException.badRequest("Please provide dischrage id ", "");
        }
    }

    
    public WardTransfer updateWardTransfer(Long id, WardTransferData data){
        WardTransfer transfer = findWardTransferById(id);
        Admission admission = admissionService.findAdmissionById(data.getAdmissionId());
        Patient patient = patientService.findPatientOrThrow(admission.getPatient().getPatientNumber());        
        Bed bed = bedService.getBed(data.getBedId());
        Room room = roomService.getRoom(bed.getRoom().getId());
        Ward ward = wardService.getWard(bed.getRoom().getWard().getId());
        BedType type = bedTypeService.getBedType(data.getBedTypeId());
        
        Bed AvailBed = admission.getBed();
        
        transfer.setPatient(patient);
        transfer.setBed(bed);
        transfer.setWard(ward);
        transfer.setRoom(room);
        transfer.setBedType(type);
        transfer.setAdmission(admission);
        
        transfer.setComment(data.getComment());
        transfer.setMethodOfTransfer(data.getMethodOfTransfer());
        transfer.setTransferDatetime(data.getTransferDatetime());
        
        AvailBed.setStatus(Bed.Status.Available);
        bed.setStatus(Bed.Status.Occupied);        
        admission.setBed(bed);
        
        bedRepository.save(AvailBed);
        admissionRepository.save(admission);
        
        return wardTransferRepository.save(transfer);
    }
}
