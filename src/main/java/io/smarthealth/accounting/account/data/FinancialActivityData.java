package io.smarthealth.accounting.account.data;

import io.smarthealth.accounting.account.domain.enumeration.AccountCategory;
import lombok.Value;

/**
 *
 * @author Kelsas
 */
@Value
public class FinancialActivityData {

    private final Integer id;
    private final String name;
    private final AccountCategory mappedAccountType;

}
