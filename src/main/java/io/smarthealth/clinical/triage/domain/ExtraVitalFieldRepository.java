package io.smarthealth.clinical.triage.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExtraVitalFieldRepository extends JpaRepository<ExtraVitalField, Long> {
    Optional<ExtraVitalField> findByName(String name);

}
