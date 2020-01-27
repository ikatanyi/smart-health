package io.smarthealth.accounting.invoice.data;

import io.smarthealth.accounting.invoice.domain.InvoiceMerge;
import io.smarthealth.debtor.claim.processing.domain.enumeration.ProcessType;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class InvoiceMergeData {

    private Long id;
    private String fromInvoiceNumber;
    
    private String toInvoiceNumber;
    @Enumerated(EnumType.STRING)
    private ProcessType type;
    private List<LineItemData> itemData;
    
    public static InvoiceMerge map(InvoiceMergeData data){
        InvoiceMerge invoiceMerge = new InvoiceMerge();
        invoiceMerge.setFromInvoiceNumber(data.getFromInvoiceNumber());
        invoiceMerge.setToInvoiceNumber(data.getToInvoiceNumber());
        invoiceMerge.setType(ProcessType.MERGE);
        return invoiceMerge;
    }
    
    public static InvoiceMergeData map(InvoiceMerge invoiceMerge){
        InvoiceMergeData data = new InvoiceMergeData();
        data.setFromInvoiceNumber(invoiceMerge.getFromInvoiceNumber());
        data.setToInvoiceNumber(invoiceMerge.getToInvoiceNumber());
        data.setType(ProcessType.MERGE);
        return data;
    }
    
}
