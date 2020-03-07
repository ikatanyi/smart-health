package io.smarthealth.debtor.claim.dispatch.data;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.debtor.claim.dispatch.domain.Dispatch;
import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class DispatchData {  
    @ApiModelProperty(required=false,hidden=true)
    private String dispatchNo;
    @ApiModelProperty(required=false,hidden=true)
    private Long id;
    private Long payerId;
    @ApiModelProperty(required=false,hidden=true)
    private String payer;
    private String comments;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate dispatchDate;
    private List<DispatchedInvoiceData>dispatchInvoiceData = new ArrayList();
    
    public static DispatchData map(Dispatch dispatch){
        DispatchData data = new DispatchData();
        data.setComments(dispatch.getComments());
        data.setDispatchDate(dispatch.getDispatchDate());
        data.setDispatchNo(dispatch.getDispatchNo());
        if(dispatch.getPayer()!=null){
            data.setPayer(dispatch.getPayer().getPayerName());
            data.setPayerId(dispatch.getPayer().getId());
        }
        for(Invoice dispInvoice:dispatch.getDispatchedInvoice()){
            DispatchedInvoiceData invoiceData=new DispatchedInvoiceData();
            invoiceData.setInvoiceNumber(dispInvoice.getNumber());
            invoiceData.setDueDate(dispInvoice.getDueDate());
            invoiceData.setInvoiceAmount(dispInvoice.getTotal());
            invoiceData.setPayerName(dispInvoice.getPayer().getPayerName());
            invoiceData.setSchemeName(dispInvoice.getPayee().getSchemeName());
            data.getDispatchInvoiceData().add(invoiceData);
        }
        return data;
    }
    
    public static Dispatch map(DispatchData data){
        Dispatch dispatch = new Dispatch();
        dispatch.setComments(data.getComments());
        return dispatch;
    }
}
