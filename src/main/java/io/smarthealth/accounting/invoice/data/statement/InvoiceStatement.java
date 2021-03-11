package io.smarthealth.accounting.invoice.data.statement;

import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Data
public class InvoiceStatement extends InterimInvoice {
    private InvoiceStatus status;
    private String dispatchNumber;
    private Boolean capitation;
    private BigDecimal invoiceBalance;

    public static InvoiceStatement of(Invoice invoice){
        InvoiceStatement invoiceStatement = new InvoiceStatement();
        invoiceStatement.setId(invoice.getId());
        invoiceStatement.setVisitNumber(invoice.getVisit().getVisitNumber());
        invoiceStatement.setVisitType(invoice.getVisit().getVisitType());
        invoiceStatement.setPayerId(invoice.getPayer().getId());
        invoiceStatement.setPayerName(invoice.getPayer().getPayerName());
        invoiceStatement.setSchemeId(invoice.getScheme().getId());
        invoiceStatement.setSchemeName(invoice.getScheme().getSchemeName());
        invoiceStatement.setMemberName(invoice.getMemberName());
        invoiceStatement.setMemberNumber(invoice.getMemberNumber());
        invoiceStatement.setPatientName(invoice.getPatient().getFullName());
        invoiceStatement.setPatientNumber(invoice.getPatient().getPatientNumber());
        invoiceStatement.setPaymentTerms(invoice.getTerms());
        invoiceStatement.setNotes(invoice.getNotes());
        invoiceStatement.setInvoiceDate(invoice.getDate());
        invoiceStatement.setInvoiceNumber(invoice.getNumber());
        invoiceStatement.setInvoiceBalance(invoice.getBalance());
        invoiceStatement.setStatus(invoice.getStatus());
        invoiceStatement.setCapitation(invoice.getCapitation());

        invoiceStatement.setItems(
                invoice.getItems().stream()
                .map(InterimInvoiceItem::map)
                .collect(Collectors.toList())
        );

        return invoiceStatement;
    }
}
