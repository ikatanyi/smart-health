/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.security.domain;

import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author kent
 */
@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long>,JpaSpecificationExecutor<AuditTrail>{
    Page<AuditTrail> findAllByCreatedOnBetween(Instant from, Instant to, Pageable pageable);
}
