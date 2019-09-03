/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.math.BigDecimal;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_ledger")
public class Ledger extends Identifiable {

    @Column(name = "ledger_type")
    private String type;
    @Column(name = "identifier")
    private String identifier;
    @Column(name = "ledger_name")
    private String name; 
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_ledger_id")
    private Ledger parentLedger;
    private BigDecimal totalValue;
    private Boolean showAccountsInChart;
}
