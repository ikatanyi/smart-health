/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.app.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

/**
 *
 * @author Simon.Waweru
 */
@Data
@Entity
public class BankBranch extends Auditable {

    @Size(max = 50)
    @Column(name = "branch_name")
    private String branchName;
    @Size(max = 50)
    @Column(name = "branch_address")
    private String branchAddress;
    @Size(max = 50)
    @Column(name = "branch_code")
    private String branchCode;
    @Size(max = 50)
    @Column(name = "branch_contact")
    private String branchContact;
    @Size(max = 50)
    @Column(name = "branch_email")
    private String branchEmail;
    @JoinColumn(name = "main_bank_id", referencedColumnName = "id"/*, insertable = false, updatable = false*/)
    @ManyToOne(optional = false)
    private MainBank mainBank;
}
