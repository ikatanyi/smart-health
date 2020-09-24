package io.smarthealth.clinical.admission.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.clinical.admission.data.BedData;
import io.smarthealth.clinical.admission.data.EmergencyContactData;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Column;
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
@Entity
@Data
@Table(name = "facility_emergency_contact")
public class EmergencyContact extends Identifiable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_emergency_contact_admission_id"))
    private Admission admission;

    public enum Relation {
        Parent,
        Spouse,
        Sibling,
        Friend
    }    
    @Enumerated(EnumType.STRING)
    private Relation relation;
    private String name;
    private String contactNumber;
    
    public EmergencyContactData toData() {
        EmergencyContactData data = new EmergencyContactData();
        data.setId(this.getId());
        if(this.getAdmission()!=null)
           data.setAdmissionId(this.getAdmission().getId());
        data.setName(this.getName());
        data.setRelation(this.getRelation());
        data.setContactNumber(this.getContactNumber());
        return data;
    }
}
