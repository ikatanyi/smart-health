package io.smarthealth.clinical.admission.service;

import io.smarthealth.clinical.admission.data.EmergencyContactData;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.domain.EmergencyContact;
import io.smarthealth.clinical.admission.domain.repository.EmergencyContactRepository;
import io.smarthealth.clinical.admission.domain.specification.EmergencyContactSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Service
@RequiredArgsConstructor
public class EmergencyContactService {

    private final EmergencyContactRepository contactRepository;
    private AdmissionService admissionService;

    public EmergencyContact createEmergencyContact(Long AdmissionId, EmergencyContactData data) {
        Admission admission = admissionService.findAdmissionById(AdmissionId);
        EmergencyContact ec = data.map();
        ec.setAdmission(admission);
        return contactRepository.save(ec);
    }

    public Page<EmergencyContact> fetchAllEmergencyContacts(Pageable page) {
        return contactRepository.findAll(page);
    }

    public Page<EmergencyContact> fetchEmergencyContacts(String name, String patientId, String term, Pageable page) {
        Specification<EmergencyContact> spec = EmergencyContactSpecification.createSpecification(name, patientId, term);
        return contactRepository.findAll(spec, page);
    }

    public EmergencyContact getEmergencyContact(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("EmergencyContact with id  {0} not found.", id));
    }

    public EmergencyContact updateEmergencyContact(Long id, EmergencyContactData data) {
        EmergencyContact contact = getEmergencyContact(id);
       
        contact.setName(data.getName());
        contact.setContactNumber(data.getContactNumber());
        contact.setRelation(data.getRelation());
        return contactRepository.save(contact);
    }
}
