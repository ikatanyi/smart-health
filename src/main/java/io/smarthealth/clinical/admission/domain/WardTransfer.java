/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.domain;

import io.smarthealth.infrastructure.domain.Auditable;
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
@Entity
@Data
@Table(name = "patient_ward_transfer")
public class WardTransfer extends Auditable{
   
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_ward_transfer_patient_id"))
    private Patient patient;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_ward_transfer_admission_id"))
    private Admission admission;
    private LocalDateTime transferDatetime; 
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_ward_transfer_ward_id"))
    private Ward ward;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_ward_transfer_room_id"))
    private Room room;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_ward_transfer_bed_id"))
    private Bed bed;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_ward_transfer_bed_type_id"))
    private BedType bedType; // to bill bed category
    private String comment;
    private String methodOfTransfer;
}
