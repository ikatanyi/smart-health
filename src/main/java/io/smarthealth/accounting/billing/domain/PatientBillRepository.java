package io.smarthealth.accounting.billing.domain;

import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface PatientBillRepository extends JpaRepository<PatientBill, Long>, JpaSpecificationExecutor<PatientBill> {
    
    Optional<PatientBill> findByBillNumber(final String identifier);
      
    @Query(value = "SELECT new  io.smarthealth.accounting.billing.domain.PatientBillGroup("
            + "b.patient.patientNumber as patientNumber,"
            + "b.patient.fullName as patientName,"
            + "b.billingDate as date,"
            + "b.visit.visitNumber as visitNumber,"
            + "sum(b.Amount),"
            + "sum(b.balance),"
            + "b.paymentMode,"
            + "b.billNumber,"
            + "b.transactionId) from PatientBill b where b.status=?1 group by b.visit.visitNumber")
    public List<PatientBillGroup> groupBy(BillStatus status);
     
    
}
