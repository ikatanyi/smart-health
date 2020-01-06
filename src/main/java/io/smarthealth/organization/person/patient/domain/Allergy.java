package io.smarthealth.organization.person.patient.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;

/**
 * Patient Allergy Record
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_allergies")
public class Allergy extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "fk_allergy_patient_id"))
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AllergyType allergyType;
    private String reaction;
    private String severity; //Critical | Mild | Danger
    private String allergen;
    private String status;
    private LocalDateTime observedDate = LocalDateTime.now();
}
