/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Simon.waweru
 */
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Page<Prescription> findByVisit(final Visit visit, final Pageable pageable);
    Page<Prescription> findByVisitAndOnDischarge(final Visit visit, Boolean onDischarge, final Pageable pageable);

    @Query("SELECT p FROM Prescription p WHERE p.id=:id")
    Prescription findPresriptionByRequestId(final Long id);
//    @Query(value = "SELECT * FROM patient_prescriptions p JOIN patient_doctor_request r ON p.id=r.id WHERE r.order_number=:orderNumber",nativeQuery = true)
//    @Query("SELECT p FROM Prescription p WHERE p.orderNumber=:orderNumber")
    List<Prescription> findByOrderNumberOrVisit(final String orderNumber, final Visit visit);
}
