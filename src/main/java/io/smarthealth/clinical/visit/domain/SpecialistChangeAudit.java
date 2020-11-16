package io.smarthealth.clinical.visit.domain;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import io.smarthealth.debtor.claim.dispatch.domain.*;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.clinical.visit.data.SpecialistChangeAuditData;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "doctor_queue_audit")
public class SpecialistChangeAudit extends Auditable {

    private String visitNumber;    
    private String fromDoctor;
    private String toDoctor;    
    private LocalDateTime date;
    private String comments;
    
    public SpecialistChangeAuditData toData(){
        SpecialistChangeAuditData data = new SpecialistChangeAuditData();
        data.setComments(this.getComments());
        data.setDate(this.date);
        data.setFromDoctor(this.fromDoctor);
        data.setToDoctor(this.toDoctor);
        data.setVisitNumber(this.visitNumber);
        return data;
    }
    
}
