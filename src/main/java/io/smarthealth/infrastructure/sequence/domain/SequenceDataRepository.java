package io.smarthealth.infrastructure.sequence.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
@Deprecated
public interface SequenceDataRepository extends JpaRepository<SequenceData, String> {

    Optional<SequenceData> findBySequenceName(String seqName);
}
