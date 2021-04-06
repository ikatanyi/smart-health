package io.smarthealth.clinical.laboratory.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.item.domain.Item;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class LabTestReagent extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_lab_test_reagent_test_id"))
    private LabTest test;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_lab_test_reagent_equipment_id"))
    private LabEquipment equipment;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_lab_test_types_reagent_service_id"))
    private Item reagentService;
}
