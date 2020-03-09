package io.smarthealth.clinical.laboratory.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Kelsas
 */
public interface LabResultRepository extends JpaRepository<LabResult, Long>, JpaSpecificationExecutor<LabResult> {

    @Query("FROM LabResult l WHERE l.labRegisterTest.labRegister.visit = ?1")
    List<LabResult> findByVisit(Visit visit);

    @Query("FROM LabResult l WHERE l.labRegisterTest.labRegister = ?1")
    List<LabResult> findByLabRegisterNumber(LabRegister register);
}
