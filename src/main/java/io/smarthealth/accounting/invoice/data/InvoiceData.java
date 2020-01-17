package io.smarthealth.accounting.invoice.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

/**
 *
 * @author Kelsas
 */
@Data
public class InvoiceData {

    private Long id;

    private String billNumber;
    private String payer;
    private Long payerId;
    private String payee;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate date;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate dueDate;
    private String terms; // 'Net 30'   
    private String transactionNo;
    private String reference;

    @NaturalId
    private String number;  //invoice number
    private String currency;
    private Boolean draft; // Outstanding true or false 
    private Boolean closed; // bad debt or not
    private Boolean paid; // fully paid or not
    private Double subtotal;
    private Double disounts;
    private Double taxes;
    private Double total;
    private Double balance;
    private String notes;

    private InvoiceStatus status;

    private List<InvoiceLineItemData> items = new ArrayList<>();

    public static InvoiceData map(Invoice invoice) {
        InvoiceData data = new InvoiceData();
        data.setId(invoice.getId());
        data.setBillNumber(invoice.getBill() != null ? invoice.getBill().getBillNumber() : "");
        data.setNumber(invoice.getNumber());
        data.setCurrency(invoice.getCurrency());
        data.setDraft(invoice.getDraft());
        data.setClosed(invoice.getClosed());
        data.setPaid(invoice.getPaid());
        data.setStatus(invoice.getStatus());
        data.setDate(invoice.getDate());
        data.setDueDate(invoice.getDueDate());
        data.setTerms(invoice.getTerms());
        data.setTransactionNo(invoice.getTransactionNo());
        data.setReference(invoice.getReference());
        data.setPayee(invoice.getPayee());

        data.setNotes(invoice.getNotes());
        data.setSubtotal(invoice.getSubtotal());
        data.setTotal(invoice.getTotal());
        data.setBalance(invoice.getBalance());

        if (invoice.getPayer() != null) {
            data.setPayer(invoice.getPayer().getPayerName());
            data.setPayerId(invoice.getPayer().getId());
        }

        if (!invoice.getItems().isEmpty()) {
            data.setItems(
                    invoice.getItems()
                            .stream()
                            .map(inv -> InvoiceLineItemData.map(inv))
                            .collect(Collectors.toList())
            );
        }
        return data;
    }
}
