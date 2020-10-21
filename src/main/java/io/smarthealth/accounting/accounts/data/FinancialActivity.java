package io.smarthealth.accounting.accounts.data;

import io.smarthealth.accounting.accounts.domain.AccountType;

/**
 *
 * @author Kelsas
 */
public enum FinancialActivity {
    Accounts_Payable("Account Payables", AccountType.LIABILITY),
    Accounts_Receivable("Account Receivable", AccountType.ASSET),
    Petty_Cash("Petty Cash", AccountType.ASSET),
    Receipt_Control("Receipt Control Account", AccountType.ASSET),
    Patient_Control("Patient Control Account", AccountType.ASSET),
    Surplus_And_Loss("Surplus And Loss", AccountType.LIABILITY),
    Cost_Of_Consultancy("Cost of Consultancy", AccountType.EXPENSE),
    Doctors_Fee("Doctor Fee", AccountType.LIABILITY),
    Tax_Payable("Tax Payable", AccountType.LIABILITY),
    Cost_Of_Sales("Cost of Sales", AccountType.EXPENSE),
    Bank_Charge("Bank Charges", AccountType.EXPENSE),
    Discount_Received("Discount Received", AccountType.REVENUE),
    Discount_Allowed("Discount Allowed", AccountType.EXPENSE),
    Copayment("Copayment", AccountType.REVENUE),
    Patient_Debt("Patient Debt", AccountType.ASSET),
    DeferredRevenue("Deferred Revenue", AccountType.LIABILITY),
    CapitationExpense("Capitation Expense", AccountType.EXPENSE),
    CapitationIncome("Capitation Income", AccountType.REVENUE);

    private final String activityName;
    private final AccountType accountType;

    private FinancialActivity(String activityName, AccountType accountType) {
        this.activityName = activityName;
        this.accountType = accountType;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public String getActivityName() {
        return activityName;
    }

}
