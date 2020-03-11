package io.smarthealth.clinical.laboratory.domain;

import io.smarthealth.clinical.laboratory.domain.enumeration.LabTestStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface LabRegisterRepository extends JpaRepository<LabRegister, Long>, JpaSpecificationExecutor<LabRegister> {

    Optional<LabRegister> findByLabNumber(String labNo);

    @Modifying
    @Query("UPDATE LabRegister r set r.status =:status WHERE r.id =:id")
    int updateLabRegisterStatus(@Param("status") LabTestStatus status, @Param("id") Long id);
}
