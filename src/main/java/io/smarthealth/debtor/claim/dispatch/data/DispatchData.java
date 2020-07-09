package io.smarthealth.debtor.claim.dispatch.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.invoice.data.InvoiceData;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.debtor.claim.dispatch.domain.Dispatch;
import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
    private LocalDate dispatchDate=LocalDate.now();
    private List<InvoiceData>dispatchInvoiceData;
    
    public static DispatchData map(Dispatch dispatch){
        DispatchData data = new DispatchData();
        data.setComments(dispatch.getComments());
        data.setDispatchDate(dispatch.getDispatchDate());
        data.setDispatchNo(dispatch.getDispatchNo());
        if(dispatch.getPayer()!=null){
            data.setPayer(dispatch.getPayer().getPayerName());
            data.setPayerId(dispatch.getPayer().getId());
        }
        data.setDispatchInvoiceData(dispatch.getDispatchedInvoice()
                .stream()
                .map((invoice)->invoice.toData())
                .collect(Collectors.toList())
        );
        
        return data;
    }
    
    public static Dispatch map(DispatchData data){
        Dispatch dispatch = new Dispatch();
        dispatch.setComments(data.getComments());
        return dispatch;
    }
}
