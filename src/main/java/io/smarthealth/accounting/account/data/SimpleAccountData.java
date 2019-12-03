package io.smarthealth.accounting.account.data;

import io.smarthealth.accounting.account.domain.Account;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class SimpleAccountData {

    private Long id;
    private String accountNumber;
    private String accountName;

    public static SimpleAccountData map(Account account) {
        SimpleAccountData accdata = new SimpleAccountData();
        accdata.setId(account.getId());
        accdata.setAccountNumber(account.getAccountNumber());
        accdata.setAccountName(account.getAccountName());

        return accdata;
    }
}
