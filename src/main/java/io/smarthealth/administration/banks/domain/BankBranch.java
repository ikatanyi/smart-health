package io.smarthealth.administration.banks.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
@Entity
@Table(name = "ref_bank_branch")
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

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_ref_bank_branch_bank_id"))
    @ManyToOne(optional = false)
    private Bank bank;
}
