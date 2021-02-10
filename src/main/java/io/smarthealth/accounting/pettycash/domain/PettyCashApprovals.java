/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.domain;

import io.smarthealth.accounting.pettycash.data.enums.PettyCashStatus;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.security.domain.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
public class PettyCashApprovals extends Auditable {

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_petty_cash_approvals_approved_by"))
    private User approvedBy;

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_petty_cash_approvals_request_no"))
    private PettyCashRequests requestNo;

    @Enumerated(EnumType.STRING)
    private PettyCashStatus approvalStatus;

    private String approvalComments;

    @OneToMany(mappedBy = "approval", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<PettyCashApprovedItems> approvedItems = new ArrayList<>();
}
