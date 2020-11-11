package io.smarthealth.clinical.visit.service;

import io.smarthealth.clinical.visit.data.SpecialistChangeAuditData;
import io.smarthealth.clinical.visit.domain.SpecialistChangeAudit;
import io.smarthealth.clinical.visit.domain.SpecialistChangeAuditRepository;
import io.smarthealth.clinical.visit.domain.specification.SpecialistChangeAuditSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpecialistChangeAuditService {

    private final SpecialistChangeAuditRepository specialistChangeAuditRepository;

    @Transactional
    public SpecialistChangeAudit createSpecialistChangeAudit(SpecialistChangeAuditData data) {
        SpecialistChangeAudit specialistChangeAudit = data.map();       
        return specialistChangeAuditRepository.save(specialistChangeAudit);
    }

    public SpecialistChangeAudit getSpecialistChangeAuditByIdWithFailDetection(Long id) {
        return specialistChangeAuditRepository.findById(id).orElseThrow(() -> APIException.notFound("SpecialistChangeAudit identified by id {0} not found ", id));
    }    

    public Optional<SpecialistChangeAudit> getSpecialistChangeAudit(Long id) {
        return specialistChangeAuditRepository.findById(id);
    }

    public Page<SpecialistChangeAudit> getAllSpecialistChangeAudits(String doctor, DateRange range, Pageable page) {
        Specification spec = SpecialistChangeAuditSpecification.createSpecification(doctor, range);
        return specialistChangeAuditRepository.findAll(spec, page);
    }

    public List<SpecialistChangeAudit> getAllSpecialistChangeAuditesNotes() {
        return specialistChangeAuditRepository.findAll();
    }
}
