package io.smarthealth.clinical.laboratory.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabTestReagentRepository extends JpaRepository<LabTestReagent, Long> {
    List<LabTestReagent> findByTestAndEquipment(LabTest labTest, LabEquipment equipment);
}
