package io.smarthealth.organization.bank.domain;

import io.smarthealth.organization.domain.Organization;
import io.smarthealth.accounting.account.domain.Account;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "organization_bank_account")
public class BankAccount extends Identifiable {

    @ManyToOne
    private Organization organization;

    private String accountName;
    private String accountNo;
    @ManyToOne 
    @JoinColumn(name = "gl_account_id", foreignKey = @ForeignKey(name = "fk_bank_account_id"))
    private Account glAccount;
    private String bank;
    private String branchCode;
    private String swiftNumber;
    private String IBAN;
    private Boolean defaultAccount; // state if this is an organization default account
    private Boolean companyAccount; // link to the company

}
