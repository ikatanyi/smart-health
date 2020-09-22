package io.smarthealth.clinical.admission.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Where;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "patient_admissions")
public class Admission extends Visit {

    private LocalDateTime admissionDate;

    private String admissionNo;

    private String admissionReason;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_pat_admisssion_ward_id"))
    private Ward ward;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_pat_admisssion_room_id"))
    private Room room;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_pat_admisssion_bed_id"))
    private Bed bed;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_pat_admisssion_bed_type_id"))
    private BedType bedType;
    
    @OneToMany(mappedBy = "admission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmergencyContact> emergencyContacts;

    @Where(clause = "voided = false")
    @OneToMany(mappedBy = "admission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CareTeam> careTeam = new ArrayList<>();

    private Boolean discharged = Boolean.FALSE;
    private String dischargedBy;
    private LocalDateTime dischargeDate;
    
    public void addEmergencyContact(EmergencyContact emergencyContact) {
        emergencyContact.setAdmission(this);
        emergencyContacts.add(emergencyContact);
    }

    public void ddEmergencyContacts(List<EmergencyContact> emergencyContacts) {
        this.emergencyContacts = emergencyContacts;
        this.emergencyContacts.forEach(x -> x.setAdmission(this));
    }
    
    public void addCareTeam(CareTeam ct) {
        ct.setAdmission(this);
        careTeam.add(ct);
    }

    public void addCareTeams(List<CareTeam> careTeam) {
        this.careTeam = careTeam;
        this.careTeam.forEach(x -> x.setAdmission(this));
    }

}
