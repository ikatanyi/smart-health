/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.approval.domain;

import io.smarthealth.approval.data.enums.ApprovalModule;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
@Entity
@Deprecated
public class ApprovalStage extends Identifiable {

    /*
    This entity though, atleast for now, somehow, it assumes per stage has one approver. 
    Deprecated because it does not guarantee request number data integrity
     */
    private String requestNo;
    private int approvalStage;
    @Enumerated(EnumType.STRING)
    private ApprovalModule moduleName;
}
