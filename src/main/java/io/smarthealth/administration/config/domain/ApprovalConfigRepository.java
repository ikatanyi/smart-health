/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.config.domain;

import io.smarthealth.administration.config.data.enums.ApprovalModule;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Simon.waweru
 */
@Repository
public interface ApprovalConfigRepository extends JpaRepository<ApprovalConfig, Long> {

    Page<ApprovalConfig> findByApprovalModule(final ApprovalModule approvalModule, final Pageable pageable);

    Optional<ApprovalConfig> findByApprovalModule(final ApprovalModule approvalModule);
}
