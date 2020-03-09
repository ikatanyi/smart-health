package io.smarthealth.clinical.laboratory.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface LabRegisterRepository extends JpaRepository<LabRegister, Long>, JpaSpecificationExecutor<LabRegister> {

    Optional<LabRegister> findByLabNumber(String labNo);
}
