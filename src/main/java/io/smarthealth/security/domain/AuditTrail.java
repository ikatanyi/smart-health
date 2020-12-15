/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.security.domain;

import io.smarthealth.security.data.AuditTrailData;
import io.smarthealth.infrastructure.domain.Auditable;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author kent
 */
@Entity
@Data
@Table(name = "audit_trail")
public class AuditTrail extends Auditable{
    private String name;
    private String description;
    
    public AuditTrailData toData(){
        AuditTrailData data = new AuditTrailData();
        data.setCreatedBy(this.getCreatedBy());
        data.setCreatedOn(this.createdOn);
        data.setDescription(this.getDescription());
        data.setName(this.getName());
        return data;
    }
}
