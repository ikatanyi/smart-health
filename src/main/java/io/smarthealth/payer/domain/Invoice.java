/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.payer.domain;

import io.smarthealth.accounting.domain.Account;
import io.smarthealth.accounting.domain.Journal;
import io.smarthealth.common.domain.Auditable;
import io.smarthealth.organization.domain.Partner;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "invoice")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Invoice extends Auditable {

    @OneToMany(mappedBy = "invoice")
    private List<InvoiceLine> invoiceLines;

    @ManyToOne
    private Journal journal;
    @ManyToOne
    private Partner partner;
    @ManyToOne
    private Account account;
    @Column(length = 16)
    private String invoiceNumber;
    private BigDecimal amount;
}
