package io.smarthealth.organization.person.patient.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
    private Patient patient;

    @NotNull
    @JoinColumn(name = "a_type")
    @ManyToOne
    private PatientIdentificationType type;
    @NotNull
    @Column(name = "a_value")
    private String value;

    private Boolean validated;
}
