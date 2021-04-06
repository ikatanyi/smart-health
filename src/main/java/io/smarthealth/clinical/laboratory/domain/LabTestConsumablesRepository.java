package io.smarthealth.clinical.laboratory.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabTestConsumablesRepository extends JpaRepository<LabTestConsumables, Long> {
    List<LabTestConsumables> findByLabRegister(final LabRegister labRegister);
}
