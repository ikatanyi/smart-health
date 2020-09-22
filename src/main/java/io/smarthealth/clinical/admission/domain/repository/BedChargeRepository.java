package io.smarthealth.clinical.admission.domain.repository;

import io.smarthealth.clinical.admission.domain.BedCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kennedy.Ikatanyi
 */
public interface BedChargeRepository extends JpaRepository<BedCharge, Long>, JpaSpecificationExecutor<BedCharge> {

}
