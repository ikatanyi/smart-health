package io.smarthealth.clinical.admission.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "patient_care_team")
public class CareTeam extends Auditable {

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_care_team_patient_id"))
    private Patient patient;
    
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_care_team_admission_id"))
    @ManyToOne(optional = false)
    private Admission admission;
    
    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_care_team_medic_id"))
    private Employee medic;
    //we can consider this as a look up
    @Enumerated(EnumType.STRING)
    private CareTeamRole careRole;
    private LocalDateTime dateAssigned;
    private Boolean isActive = Boolean.TRUE;

}
