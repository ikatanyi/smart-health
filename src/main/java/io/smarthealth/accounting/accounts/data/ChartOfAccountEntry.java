package io.smarthealth.accounting.accounts.data;

import io.smarthealth.accounting.accounts.domain.AccountType;
import lombok.Data;

@Data
public class ChartOfAccountEntry {

    private String code;
    private String name;
    private String description;
    private AccountType type;
    private Integer level;

}
