package io.smarthealth.accounting.invoice.domain;

/**
 *
 * @author Kelsas
 */
public enum InvoiceStatus {
    Draft,
    Sent,
    Paid,
    PartialPaid,
    Voided,
    WrittenOff
}
