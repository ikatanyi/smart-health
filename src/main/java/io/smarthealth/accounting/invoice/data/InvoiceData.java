package io.smarthealth.accounting.invoice.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class InvoiceData {
    private Long id;
    private String payer;
    private String number;
    private String name;
    private String currency;
    private Boolean draft;
    private Boolean closed;
    private Boolean paid;
    private InvoiceStatus status;

    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate date;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate dueDate;
    private String paymentTerms;
    private List<LineItemData> items = new ArrayList<>();
    private String notes; // additional notes displayed on invoice
    private Double subtotal;
//    private List<Discount> disounts;
//    private List<Tax> taxes;
    private Double total;
    private Double balance;
    
    public static InvoiceData map(Invoice invoice){
        InvoiceData data=new InvoiceData();
        data.setId(invoice.getId());
        data.setPayer(invoice.getPayer());
        data.setNumber(invoice.getNumber());
        data.setName(invoice.getName());
        data.setCurrency(invoice.getCurrency());
        data.setDraft(invoice.getDraft());
        data.setClosed(invoice.getClosed());
        data.setPaid(invoice.getPaid());
        data.setStatus(invoice.getStatus());
        data.setDate(invoice.getDate());
        data.setDueDate(invoice.getDueDate());
        data.setPaymentTerms(invoice.getPaymentTerms());
        data.setNotes(invoice.getNotes());
        data.setSubtotal(invoice.getSubtotal());
        data.setTotal(invoice.getTotal());
        data.setBalance(invoice.getBalance());
        
        if(!invoice.getItems().isEmpty()){
           data.setItems(
                   invoice.getItems()
                    .stream()
                    .map(inv -> LineItemData.map(inv))
                    .collect(Collectors.toList())
           );
        }
        return data;
    }
}
