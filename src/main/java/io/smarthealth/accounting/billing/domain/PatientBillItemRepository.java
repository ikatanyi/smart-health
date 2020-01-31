package io.smarthealth.accounting.billing.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Kelsas
 */
public interface PatientBillItemRepository extends JpaRepository<PatientBillItem, Long>{
    
    @Query(value = "SELECT p from PatientBillItem p WHERE p.patientBill.visit=:visit")
    Page<PatientBillItem> findPatientBillItemByVisit(Visit visit, Pageable page);
}
