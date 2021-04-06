package io.smarthealth.clinical.laboratory.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class LabEquipment extends Auditable {
    private String equipmentName;

}
