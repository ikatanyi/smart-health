package io.smarthealth.accounting.account.domain.enumeration;

import java.util.List;
import io.smarthealth.accounting.account.data.FinancialActivityData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Kelsas
 */
public enum FinancialActivity {

    ACCOUNTS_RECEIVABLE(100, "accountsReceivable", AccountCategory.ASSET),
    ACCOUNTS_PAYABLE(200, "accountsPayable", AccountCategory.LIABILITY),
    PETTY_CASH(101, "pettyCash", AccountCategory.ASSET),
    RECEIPT_CONTROL(102, "receiptControl", AccountCategory.ASSET),
    BANK(103, "defaultBank", AccountCategory.ASSET),
    PATIENT_INVOICE_CONTROL(104, "patientInvoiceControl", AccountCategory.ASSET),
    SUSPENCE_ACCOUNT(201, "suspenceAccount", AccountCategory.LIABILITY),
    SURPLUS_AND_LOSS(202, "surplusAndLoss", AccountCategory.LIABILITY),
    STOCK_LEDGER(105, "accountsPayable", AccountCategory.ASSET);

    private final Integer value;
    private final String code;
    private final AccountCategory mappedAccountCategory;
    private static List<FinancialActivityData> financialActivities;

    private FinancialActivity(Integer value, String code, AccountCategory mappedAccountCategory) {
        this.value = value;
        this.code = code;
        this.mappedAccountCategory = mappedAccountCategory;
    }

    public Integer getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public AccountCategory getMappedAccountCategory() {
        return mappedAccountCategory;
    }

    public String getValueAsString() {
        return this.value.toString();
    }

    private static final Map<Integer, FinancialActivity> intToEnumMap = new HashMap<>();

    static {
        for (final FinancialActivity type : FinancialActivity.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static FinancialActivity fromInt(final int financialActivityId) {
        final FinancialActivity type = intToEnumMap.get(financialActivityId);
        return type;
    }

    public static FinancialActivityData toFinancialActivityData(final int financialActivityId) {
        final FinancialActivity type = fromInt(financialActivityId);
        return convertToFinancialActivityData(type);
    }

    public static List<FinancialActivityData> getAllFinancialActivities() {
        if (financialActivities == null) {
            financialActivities = new ArrayList<>();
            for (final FinancialActivity type : FinancialActivity.values()) {
                FinancialActivityData financialActivityData = convertToFinancialActivityData(type);
                financialActivities.add(financialActivityData);
            }
        }
        return financialActivities;
    }

    private static FinancialActivityData convertToFinancialActivityData(final FinancialActivity type) {
        FinancialActivityData financialActivityData = new FinancialActivityData(type.value, type.code, type.getMappedAccountCategory());
        return financialActivityData;
    }
}
