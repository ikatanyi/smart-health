package io.smarthealth.clinical.triage.domain;

import io.smarthealth.clinical.record.domain.VitalsRecord;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.organization.person.patient.domain.Patient;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "patient_vitals_extra_fields_values")
public class ExtraVitalValue extends Identifiable {

    @ManyToOne
    @JoinColumn(name = "vital_id", foreignKey = @ForeignKey(name = "fk_extra_vital_value_vital_id"))
    private VitalsRecord vitalRecord;

    @ManyToOne
    @JoinColumn(name = "field_id", foreignKey = @ForeignKey(name = "fk_extra_vital_value_field_id"))
    private ExtraVitalField field;

    private  String value;
}
