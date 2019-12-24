package io.smarthealth.accounting.invoice.domain;

/**
 *
 * @author Kelsas
 */
public enum InvoiceStatus {
    draft,
    not_sent,
    sent,
    viewed,
    past_due,
    pending,
    paid,
    voided
}
