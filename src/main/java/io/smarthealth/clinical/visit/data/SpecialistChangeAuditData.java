package io.smarthealth.clinical.visit.data;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import io.smarthealth.clinical.visit.domain.SpecialistChangeAudit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "doctor_queue_audit")
public class SpecialistChangeAuditData extends Auditable {
    @ApiModelProperty(hidden=true)
    private Long id;
    private String visitNumber;    
    private String fromDoctor;
    private String toDoctor;    
    private LocalDateTime date=LocalDateTime.now();
    private String comments;
    
    public SpecialistChangeAudit map(){
        SpecialistChangeAudit data = new SpecialistChangeAudit();
        data.setComments(this.getComments());
        data.setDate(this.date);
        data.setFromDoctor(this.fromDoctor);
        data.setToDoctor(this.toDoctor);
        data.setVisitNumber(this.visitNumber);
        return data;
    }
    
}
