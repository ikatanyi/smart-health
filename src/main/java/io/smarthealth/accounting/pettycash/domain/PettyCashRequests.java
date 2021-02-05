/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.domain;

import io.smarthealth.accounting.pettycash.data.enums.PettyCashStatus;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Employee;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
@Entity
public class PettyCashRequests extends Auditable {

    private double totalAmount;
    private String requestNo;
    private String narration;
    private String approvalComments;

    @Enumerated(EnumType.STRING)
    private PettyCashStatus status;

    private boolean paid = false;

    private int approvalPendingLevel;

    private Double approvedAmount;

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_petty_cash_requests_request_by"))
    private Employee requestedBy;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "requestNo")
    private List<PettyCashRequestItems> pettyCashRequestItems;
}
