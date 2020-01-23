package io.smarthealth.organization.bank.data;

import io.smarthealth.accounting.acc.data.v1.AccountData;
import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.organization.bank.domain.enumeration.BankType;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class BAccountData {

    private Long id;
    @ApiModelProperty(required = false, hidden = true)
    @Enumerated(EnumType.STRING)
    private BankType bankType;
    private String accountName;
    private String accountNumber;

    private String currency;
    private String bankName;
    private String bankBranch;
    private String description;

    private String ledgerAccount;
    private String ledgerName;
    private Boolean isDefault;

    public static BAccountData map(BankAccount data) {
        BAccountData bank = new BAccountData();
        bank.setId(data.getId());
        bank.setBankName(data.getBankName());
        bank.setAccountNumber(data.getAccountNumber());
        bank.setAccountName(data.getAccountName());
        bank.setBankBranch(data.getBankBranch());
        bank.setIsDefault(data.getIsDefault());
        bank.setCurrency(data.getCurrency());
        if (data.getLedgerAccount() != null) {
            bank.setLedgerAccount(data.getLedgerAccount().getIdentifier());
            bank.setLedgerName(data.getLedgerAccount().getName());
        }

        bank.setDescription(data.getDescription());
        bank.setBankType(data.getBankType());

        return bank;
    }
}
