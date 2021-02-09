package io.smarthealth.clinical.triage.domain;

import io.smarthealth.clinical.triage.data.ExtraVitalFieldsEnums;
import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "patient_vitals_extra_fields")
public class ExtraVitalField extends Identifiable {
    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private String label;
    @Enumerated(EnumType.STRING)
    private ExtraVitalFieldsEnums type;//Number, TextField,TextArea, DropDown

    private Boolean required =Boolean.FALSE; //true,false
}
