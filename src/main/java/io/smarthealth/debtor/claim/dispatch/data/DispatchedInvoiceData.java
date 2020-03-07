package io.smarthealth.debtor.claim.dispatch.data;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class DispatchedInvoiceData {    
    
    private String invoiceNumber;
    @ApiModelProperty(required=false,hidden=true)
    private String payerName;
    @ApiModelProperty(required=false,hidden=true)
    private String schemeName;
    @ApiModelProperty(required=false,hidden=true)
    private Double invoiceAmount;
    @ApiModelProperty(required=false,hidden=true)
    private LocalDate dueDate;
}
