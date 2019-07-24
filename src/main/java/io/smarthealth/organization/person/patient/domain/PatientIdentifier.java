package io.smarthealth.organization.person.patient.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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

    @Column(name = "a_type")
    private String type;

    @Column(name = "a_value")
    private String value;

    private Boolean validated;
}
