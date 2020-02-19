/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.config.domain;

import io.smarthealth.administration.config.data.enums.ApprovalModule;
import io.smarthealth.administration.config.data.enums.ApproversPriority;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Employee;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Entity
@Data
public class ModuleApprovers extends Auditable {

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_module_approvers_employee_id"))
    private Employee employee;

    private int approvalLevel;

    @Enumerated(EnumType.STRING)
    private ApprovalModule moduleName;

}
