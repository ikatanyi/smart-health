/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.config.domain;

import io.smarthealth.administration.config.data.enums.ApprovalModule;
import io.smarthealth.infrastructure.domain.Auditable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Entity
@Data
public class ApprovalConfig extends Auditable {

    private int noOfApproveres;

    private int minNoOfApprovers;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private ApprovalModule approvalModule;

}
