/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.app.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;
import java.util.List;


/**
 *
 * @author Simon.Waweru
 */
@Data
@Entity
public class MainBank extends Auditable {

    @Size(max = 50)
    @Column(name = "bank_name")
    private String bankName;
    @Size(max = 50)
    @Column(name = "bank_code")
    private String bankCode;
    @Size(max = 50)
    @Column(name = "bank_short_name")
    private String bankShortName;
//    @JoinColumn(name = "account_no_id", referencedColumnName = "id")
//    @ManyToOne
//    private Account accountNo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mainBank")
    private List<BankBranch> bankBranch;
}
