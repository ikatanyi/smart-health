package io.smarthealth.infrastructure.sequence.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *
 * @author Kelsas
 */
public interface SequenceDataRepository extends JpaRepository<SequenceData, String> {

    Optional<SequenceData> findBySequenceName(String seqName);
}
