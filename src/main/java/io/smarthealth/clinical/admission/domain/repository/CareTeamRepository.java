/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.domain.repository;

import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.domain.CareTeam;
import java.util.List;
import java.util.Optional;

import io.smarthealth.clinical.admission.domain.CareTeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Simon.waweru
 */
public interface CareTeamRepository extends JpaRepository<CareTeam, Long>,JpaSpecificationExecutor<CareTeam> {
    List<CareTeam> findByAdmission(final Admission admission);

    Optional<CareTeam> findCareTeamByAdmission_AdmissionNoAndCareRole(String admissionNo, CareTeamRole role);
}
