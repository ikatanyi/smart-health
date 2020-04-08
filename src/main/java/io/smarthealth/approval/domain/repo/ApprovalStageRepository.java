/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.approval.domain.repo;

import io.smarthealth.approval.data.enums.ApprovalModule;
import io.smarthealth.approval.domain.ApprovalStage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Simon.waweru
 */
@Repository
@Deprecated
public interface ApprovalStageRepository extends JpaRepository<ApprovalStage, Long> {

    List<ApprovalStage> findByModuleName(final ApprovalModule moduleName);

    Optional<ApprovalStage> findByModuleNameAndRequestNo(final ApprovalModule moduleName, final String requestNo);

}
