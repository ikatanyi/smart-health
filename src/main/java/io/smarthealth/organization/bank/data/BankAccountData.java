package io.smarthealth.organization.bank.data;

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
public class BankAccountData {

    private Long id;
    @ApiModelProperty(required = false, hidden = true)
    @Enumerated(EnumType.STRING)
    private BankType bankType;
    private String accountName;
    private String accountNumber;

    private String currency;
    private Long bankId;
    @ApiModelProperty(required = false, hidden = true)
    private String bankName;
    private Long branchId;
    @ApiModelProperty(required = false, hidden = true)
    private String bankBranch;
    private String description;

    private String ledgerAccount;
    private String ledgerName;
    private Boolean isDefault;

    public static BankAccountData map(BankAccount data) {
        BankAccountData bank = new BankAccountData();
        bank.setId(data.getId());
        if(data.getBank()!=null){
            bank.setBankName(data.getBank().getBankName());
        }
        bank.setAccountNumber(data.getAccountNumber());
        bank.setAccountName(data.getAccountName());
        if(data.getBankBranch()!=null){
            bank.setBankBranch(data.getBankBranch().getBranchName());
        }
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
