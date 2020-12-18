/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.security.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.security.domain.AuditTrailRepository;
import io.smarthealth.security.data.AuditTrailData;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import io.smarthealth.security.domain.AuditTrail;
import io.smarthealth.security.domain.specification.AuditTrailSpecification;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author kent
 */
@Service
@RequiredArgsConstructor
public class AuditTrailService {
    private final AuditTrailRepository auditTrailRepository;
    
    public AuditTrail createAuditTrail(AuditTrailData data){
        AuditTrail auditTrail  = data.map();
        return auditTrailRepository.save(auditTrail);
    } 
    
    public AuditTrail saveAuditTrail(String name, String description){
        AuditTrail auditTrail  = new AuditTrail();
        auditTrail.setName(name);
        auditTrail.setDescription(description);
        return auditTrailRepository.save(auditTrail);
    } 
   

    public Page<AuditTrail> findAll(DateRange range, String name, Pageable pageable) {
        Specification<AuditTrail>spec=AuditTrailSpecification.createSpecification(range, name);
        return auditTrailRepository.findAll(spec, pageable);
    }

    public AuditTrail find(Long id) {
        return auditTrailRepository.findById(id)
                 .orElseThrow(() -> APIException.notFound("Audit Trail with id {0} not found", id));
    }
}
