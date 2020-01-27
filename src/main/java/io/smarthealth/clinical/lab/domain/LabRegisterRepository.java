/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface LabRegisterRepository extends JpaRepository<LabRegister, Long>, JpaSpecificationExecutor<LabRegister> {

    @Query("SELECT pt FROM LabRegister pt WHERE pt.visit.visitNumber=:visitNumber")
    Page<LabRegister> findPatientTests(@Param("visitNumber") final String visitNo, final Pageable pageable);

    Optional<LabRegister> findByAccessNo(final String accessNo);

    List<LabRegister> findByVisit(final Visit visit);

}
