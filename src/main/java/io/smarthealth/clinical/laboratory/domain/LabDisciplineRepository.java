package io.smarthealth.clinical.laboratory.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface LabDisciplineRepository extends JpaRepository<LabDiscipline, Long> {

    Optional<LabDiscipline> findByDisplineName(final String discplineName);
}
