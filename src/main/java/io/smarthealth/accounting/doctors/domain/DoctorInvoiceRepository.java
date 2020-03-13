package io.smarthealth.accounting.doctors.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface DoctorInvoiceRepository extends JpaRepository<DoctorInvoice, Long>, JpaSpecificationExecutor<DoctorInvoice> {

    @Query("SELECT d FROM DoctorInvoice d where d.invoiceNumber=:inv AND d.doctor.id=:docId")
    Optional<DoctorInvoice> findByInvoiceForDoctor(@Param("inv") String invNo, @Param("docId") Long docId);
}
