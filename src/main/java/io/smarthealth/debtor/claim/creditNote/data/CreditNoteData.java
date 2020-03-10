package io.smarthealth.debtor.claim.creditNote.data;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.debtor.claim.creditNote.domain.CreditNote;
import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class CreditNoteData {  
    @ApiModelProperty(required=false, hidden=true)
    private Long id;
    @ApiModelProperty(required=false, hidden=true)
    private String creditNoteNo;
    private Double amount;
    private Long payerId;
    @ApiModelProperty(required=false, hidden=true)
    private String payer;
    private String comments;
    private String invoiceNo;
    @ApiModelProperty(required=false, hidden=true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate invoiceDate;
    @ApiModelProperty(required=false, hidden=true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate date;
    private List<CreditNoteItemData>billItems;
    
    public static CreditNote map(CreditNoteData data){
        CreditNote creditNote = new CreditNote();
        creditNote.setAmount(data.getAmount());
        creditNote.setComments(data.getComments());
        return creditNote;
    }
    
}
