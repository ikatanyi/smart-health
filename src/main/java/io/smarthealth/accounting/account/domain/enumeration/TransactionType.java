package io.smarthealth.accounting.account.domain.enumeration;

/**
 *
 * @author Kelsas
 */
public enum TransactionType {

    Journal_Entry("Journal Entry"),
    Journal_Reversal("Journal Reversal"),
    Invoicing("Invoicing"),
    Billing("Billing");

    private final String label;

    private TransactionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
