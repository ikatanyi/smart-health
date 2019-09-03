package io.smarthealth.financial.account.domain.enumeration;

import java.util.List;
import io.smarthealth.financial.account.data.FinancialActivityData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Kelsas
 */
public enum FinancialActivity {

    ACCOUNTS_RECEIVABLE(100, "accountsReceivable", AccountType.ASSET),
    
    ACCOUNTS_PAYABLE(200, "accountsPayable", AccountType.LIABILITY),
    
    PETTY_CASH(101, "pettyCash", AccountType.ASSET),
    
    RECEIPT_CONTROL(102, "receiptControl", AccountType.ASSET),
    
    BANK(103, "defaultBank", AccountType.ASSET),
    
    PATIENT_INVOICE_CONTROL(104, "patientInvoiceControl", AccountType.ASSET),
    
    SUSPENCE_ACCOUNT(201, "suspenceAccount", AccountType.LIABILITY),
    
    SURPLUS_AND_LOSS(202, "surplusAndLoss", AccountType.LIABILITY),
    
    STOCK_LEDGER(105, "accountsPayable", AccountType.ASSET);

    private final Integer value;
    private final String code;
    private final AccountType mappedAccountType;
    private static List<FinancialActivityData> financialActivities;

    private FinancialActivity(Integer value, String code, AccountType mappedAccountType) {
        this.value = value;
        this.code = code;
        this.mappedAccountType = mappedAccountType;
    }

    public Integer getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public AccountType getMappedAccountType() {
        return mappedAccountType;
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
        FinancialActivityData financialActivityData = new FinancialActivityData(type.value, type.code, type.getMappedAccountType());
        return financialActivityData;
    }
}
