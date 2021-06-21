package io.smarthealth.clinical.laboratory.domain;

import io.smarthealth.clinical.laboratory.domain.enumeration.LabTestStatus;
import io.smarthealth.clinical.radiology.domain.TotalTest;
import io.smarthealth.security.util.SecurityUtils;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import io.smarthealth.clinical.laboratory.domain.LabTestPanel;

/**
 *
 * @author Kelsas
 */
public interface LabRegisterTestRepository extends JpaRepository<LabRegisterTest, Long>, JpaSpecificationExecutor<LabRegisterTest> {

    @Modifying
    @Query("UPDATE LabRegisterTest t SET t.collected=true, t.collectionDateTime=CURRENT_TIMESTAMP, t.collectedBy=:collectedBy, t.specimen=:specimen, t.status=:status WHERE t.id=:id")
    int updateTestCollected(@Param("collectedBy") String collectedBy, @Param("specimen") String specimen, @Param("id") Long id, @Param("status") LabTestStatus status);

    @Modifying
    @Query("UPDATE LabRegisterTest t SET t.entered=true, t.entryDateTime=CURRENT_TIMESTAMP, t.enteredBy=:enteredBy, t.status=:status WHERE t.id=:id")
    int updateTestEntry(@Param("enteredBy") String enteredBy, @Param("id") Long id, @Param("status") LabTestStatus status);

    @Modifying
    @Query("UPDATE LabRegisterTest t SET t.validated=true, t.validationDateTime=CURRENT_TIMESTAMP, t.validatedBy=:validatedBy,t.status=:status WHERE t.id=:id")
    int updateTestValidation(@Param("validatedBy") String validatedBy, @Param("id") Long id, @Param("status") LabTestStatus status);

    @Modifying
    @Query("UPDATE LabRegisterTest t SET t.paid=true WHERE t.id=:id")
    int updateTestPaid(@Param("id") Long id);

    @Query("SELECT t FROM LabRegisterTest t WHERE t.labRegister.visit.visitNumber =:visitNo")
    List<LabRegisterTest> findTestsByVisitNumber(@Param("visitNo") String visitNo);

    @Query("SELECT t FROM LabRegisterTest t WHERE t.labRegister.visit.visitNumber =:visitNo AND t.labRegister.labNumber=:labNo")
    List<LabRegisterTest> findTestsByVisitAndLabNo(@Param("visitNo") String visitNo, @Param("labNo") String labNo);

    Long countByStatus(LabTestStatus status);

    @Modifying
    @Query("UPDATE LabRegisterTest r SET r.attachment =:attachment WHERE r.id=:id")
    int addAttachment(@Param("attachment") String attachment, @Param("id") Long id);
    
    List<LabRegisterTest> findByLabTest(@Param("labtest")LabTest labtest);
    
    List<LabRegisterTest> findByLabTestAndEntryDateTimeBetween(@Param("labtest")LabTest labtest, LocalDateTime date1, LocalDateTime date2);
    
    @Query("SELECT t FROM LabRegisterTest t WHERE t.entryDateTime BETWEEN :frmdt AND :todt GROUP BY t.labTest")
    List<LabRegisterTest> findTestsByDateRange(@Param("frmdt") LocalDateTime from, @Param("todt") LocalDateTime todt);

    @Query("SELECT t FROM LabRegisterTest t WHERE t.parentLabTest.id =:labtest AND t.parentLabTest<>null AND t.entryDateTime BETWEEN :frmdt AND :todt GROUP BY t.parentLabTest, t.labRegister.patientNo")
    List<LabRegisterTest> findPanelTestsByDateRange(@Param("labtest") Long labtest, @Param("frmdt") LocalDateTime from, @Param("todt") LocalDateTime todt);

    @Query("SELECT t FROM LabTest t WHERE t.isPanel=true group by t.id")
    List<LabTest> findPanels();


    @Query("SELECT d.labTest.testName as testName, count(d.labTest.testName) AS count, SUM(d.price) as totalPrice FROM LabRegisterTest d LEFT JOIN LabTest t ON d.labTest.id= t.id WHERE d.labRegister.createdOn BETWEEN :fromDate AND :toDate Group by d.labTest.testName")
    List<TotalTest>findTotalTests(@Param("fromDate")Instant fromDate, @Param("toDate")Instant toDate);

    @Query("SELECT d.labTest.testName as testName, d.labRegister.requestedBy as practitioner, count(d.labTest.testName) AS count, SUM(d.price) as totalPrice FROM LabRegisterTest d WHERE d.labRegister.createdOn BETWEEN :fromDate AND :toDate Group by d.labRegister.requestedBy")
    List<TotalTest>findTotalTestsByPractitioner(@Param("fromDate")Instant fromDate, @Param("toDate")Instant toDate);

}
