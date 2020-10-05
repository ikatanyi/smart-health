package io.smarthealth.clinical.admission.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.clinical.admission.data.NhifRebateData;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kent
 */
@Entity
@Data
@Table(name = "nhif_rebate")
public class NhifRebate extends Identifiable {

    @OneToOne
    private Patient patient;
    private Double amount;
    private Double rate;
    @OneToOne
    private Admission admission;
    private Integer duration;
    private String memberNumber; 
    private LocalDateTime date;
    
    public NhifRebateData toData(){
        NhifRebateData data = new NhifRebateData();
        if(this.getAdmission()!=null){
            data.setAdmissionNumber(this.getAdmission().getAdmissionNo());
            
        }
        data.setAmount(this.getAmount());
        data.setDate(this.getDate());
        data.setRate(this.getRate());
        data.setDuration(this.getDuration());
        data.setMemberNumber(this.getMemberNumber());
        data.setPatientName(this.getPatient().getFullName());
        data.setPatientNumber(this.getPatient().getPatientNumber());
        return data;
    }
}
