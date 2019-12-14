package io.smarthealth.organization.person.patient.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * Patient Identifiers
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_identification")
public class PatientIdentifier extends Identifiable {

    @ManyToOne
    @JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "fk_pat_tag_patient_id"))
    private Patient patient;

    @NotNull 
    @ManyToOne
    @JoinColumn(name = "a_type", foreignKey = @ForeignKey(name = "fk_pat_tag_type_id"))
    private PatientIdentificationType type;
    @NotNull
    @Column(name = "a_value")
    private String value;

    private Boolean validated;
}
