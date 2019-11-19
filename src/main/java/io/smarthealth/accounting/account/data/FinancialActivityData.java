package io.smarthealth.accounting.account.data;

import io.smarthealth.accounting.account.domain.enumeration.FinancialActivity;
import lombok.Value;

/**
 *
 * @author Kelsas
 */
@Value
public class FinancialActivityData {

    private FinancialActivity activity;
    private final String name;
    private final String category;

    public static FinancialActivityData map(FinancialActivity activity) {
        return new FinancialActivityData(activity, activity.getActivityName(), activity.getCategory().name());
    }
}
