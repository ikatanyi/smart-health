package io.smarthealth.accounting.billing.domain;

import io.smarthealth.accounting.billing.data.PatientBillGroup;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.clinical.visit.domain.Visit;
import java.util.List;
import java.util.Optional;

import io.smarthealth.integration.metadata.PatientData.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface PatientBillRepository extends JpaRepository<PatientBill, Long>, JpaSpecificationExecutor<PatientBill>, VisitBillSummaryRepository {

    Optional<PatientBill> findByBillNumber(final String identifier);

    List<PatientBill> findByVisit(Visit visit);

    @Query(value = "SELECT new  io.smarthealth.accounting.billing.data.PatientBillGroup("
            + "b.patient.patientNumber as patientNumber,"
            + "b.patient.fullName as patientName,"
            + "b.billingDate as date,"
            + "b.visit.visitNumber as visitNumber,"
            + "sum(b.amount),"
            + "sum(b.balance),"
            + "b.paymentMode,"
            + "b.billNumber,"
            + "b.transactionId) from PatientBill b where b.status=?1 group by b.visit.visitNumber")
    public List<PatientBillGroup> groupBy(BillStatus status);

    Optional<PatientBill> findPatientBillByVisit(Visit visit);

    Optional<PatientBill> findPatientBillByReference(String reference);

    Optional<PatientBill> findPatientBillByVisitAndStatus(Visit visit, BillStatus status);

    Optional<Patient> findPatientBillByReferenceAndStatus(String reference, BillStatus status);


}
