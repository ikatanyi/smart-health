package io.smarthealth.accounting.invoice.data.statement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceSummaryItem {
    private String service;
    private BigDecimal totalAmount;
}
