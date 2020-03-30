/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface ProcedureTestRepository extends JpaRepository<PatientProcedureTest, Long>{
    @Modifying
    @Query("UPDATE PatientProcedureTest d SET d.paid=true WHERE d.id=:id")
    int updateProcedurePaid(@Param("id") Long id);
}
