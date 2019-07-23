package io.smarthealth.company.bank.domain;

import io.smarthealth.company.domain.Organization; 
import io.smarthealth.financial.account.domain.Account;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
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
    @OneToOne
    private Account glAccount;
    private String bank;
    private String branchCode;
    private String swiftNumber;
    private String IBAN;
    private Boolean defaultAccount; // state if this is an organization default account
    private Boolean companyAccount; // link to the company
 
}
