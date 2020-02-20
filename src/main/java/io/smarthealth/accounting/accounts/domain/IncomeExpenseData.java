package io.smarthealth.accounting.accounts.domain;

import io.smarthealth.accounting.accounts.data.SimpleAccountData;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class IncomeExpenseData implements Serializable {

    private String code = "0";
    private String message = "success";
    private List<SimpleAccountData> incomeAccounts;
    private List<SimpleAccountData> expensesAccounts;

}
