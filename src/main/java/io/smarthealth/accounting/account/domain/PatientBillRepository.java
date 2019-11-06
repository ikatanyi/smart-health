/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface PatientBillRepository extends JpaRepository<PatientBill, Long> {
    
    Optional<PatientBill> findByBillNumber(final String identifier);
    
    @Query("SELECT c FROM PatientBill c WHERE (:billNumber is null or c.billNumber = :billNumber) AND (:visit is null or c.visit = :visitNumber) AND (:paymentMode is null or c.paymentMode = :paymentMode) AND (:referenceNumber is null or c.referenceNumber = :referenceNumber)")
    Page<PatientBill> findBill(@Param("billNumber") String billNumber, @Param("visit") Visit visit, @Param("paymentMode") String paymentMode, @Param("referenceNumber") String referenceNumber, Pageable page);
}
