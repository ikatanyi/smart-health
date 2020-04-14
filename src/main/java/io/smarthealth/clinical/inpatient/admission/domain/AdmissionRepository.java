package io.smarthealth.clinical.inpatient.admission.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface AdmissionRepository extends JpaRepository<Admission, Long> {

    @Modifying
    @Query("UPDATE Admission a SET a.status=:status, a.dischargeDate=CURRENT_TIMESTAMP WHERE a.admissionNo=:admissionNo")
    int updateAdmissionStatus(@Param("admissionNo") String admissionNo, @Param("status") Admission.Status status);

    @Modifying
    @Query("UPDATE Admission a SET a.voided=true, a.voidedDate=CURRENT_TIMESTAMP, a.voidedBy=:voidedBy WHERE a.admissionNo=:admissionNo")
    int voidAdmission(@Param("admissionNo") String admissionNo, @Param("voidedBy") String voidedBy);

    Optional<Admission> findByAdmissionNo(String admissionNumber);
    
}
