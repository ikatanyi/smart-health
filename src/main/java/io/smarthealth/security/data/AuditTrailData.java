/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.security.data;
import io.smarthealth.security.domain.AuditTrail;
import io.swagger.annotations.ApiModelProperty;
import java.time.Instant;
import lombok.Data;

/**
 *
 * @author kent
 */
@Data
public class AuditTrailData{
    private String name;
    private String description;
    @ApiModelProperty(hidden=true)
    private String createdBy;
    @ApiModelProperty(hidden=true)
    private Instant createdOn;
    
    public AuditTrail map(){
        AuditTrail auditTrail = new AuditTrail();
        auditTrail.setName(this.getName());
        auditTrail.setDescription(this.getDescription());
        return auditTrail;
    }
}
