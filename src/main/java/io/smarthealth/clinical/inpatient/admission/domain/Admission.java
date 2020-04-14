package io.smarthealth.clinical.inpatient.admission.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.clinical.inpatient.admission.data.AdmissionData;
import io.smarthealth.clinical.inpatient.setup.domain.Bed;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
@Table(name = "patient_admission")
public class Admission extends Auditable {

    public enum Status {
        Admitted,
        Transferred,
        Discharged,
    }

    public enum Type {
        Elective,
        Routine,
        Urgent,
        Maternity,
        Emergency
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_admission_patient_id"))
    private Patient patient;

    private String admissionNo;

    private LocalDateTime admissionDate;

    private LocalDateTime dischargeDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_admission_medic_id"))
    private Employee medic;

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_admission_bed_id"))
    @ManyToOne
    private Bed bed;

    private String admissionReason;

    private Boolean voided = Boolean.FALSE;
    private String voidedBy;
    private LocalDateTime voidedDate;

    public AdmissionData toData() {
        AdmissionData data = new AdmissionData();
        data.setId(this.getId());
        data.setAdmissionDate(this.admissionDate);
        data.setAdmissionNo(this.admissionNo);
        data.setAdmissionReason(this.admissionReason);
        data.setAdmissionType(this.type);
        if (this.medic != null) {
            data.setAdmittingDoctor(this.medic.getFullName());
            data.setAdmittingDoctorId(this.medic.getId());
        }
        if (this.bed != null) {
            data.setBed(this.bed.getName());
            data.setBedId(this.bed.getId());
            data.setRoom(this.bed.getRoom().getName());
            data.setRoomId(this.bed.getRoom().getId());
            data.setWard(this.bed.getRoom().getWard().getName());
            data.setWardId(this.bed.getRoom().getWard().getId());
        }
        data.setDischargeDate(this.dischargeDate);
        if (this.patient != null) {
            data.setPatientName(this.patient.getFullName());
            data.setPatientNumber(this.patient.getPatientNumber());
        }
        data.setStatus(this.status);
        return data;
    }
}
