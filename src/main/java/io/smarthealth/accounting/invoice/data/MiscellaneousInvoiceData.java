/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.invoice.data;

import io.smarthealth.accounting.invoice.domain.MiscellaneousInvoice;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class MiscellaneousInvoiceData {

    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private Long payerId;
    private String payerName;
    private Long schemeId;
    private String schemeName;
    private String reference;
    private List<MiscellaneousInvoiceItemData> lineItems = new ArrayList<>();

    public static MiscellaneousInvoiceData map(MiscellaneousInvoice inv) {
        MiscellaneousInvoiceData data = new MiscellaneousInvoiceData();
        return data;
    }
}
