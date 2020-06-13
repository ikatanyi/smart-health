package io.smarthealth.debtor.claim.creditNote.data;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class CreditNoteItemData { 
    private Long invoiceItemId;//bill_item_id
    @ApiModelProperty(required=false, hidden=true)
    private Long id;
    @ApiModelProperty(required=false, hidden=true)
    private Long itemId;
    @ApiModelProperty(required=false, hidden=true)
    private String itemName;
    @ApiModelProperty(required=false, hidden=true)
    private String itemCode;
    @ApiModelProperty(required=false, hidden=true)
    private Double quantity;
    @ApiModelProperty(required=false, hidden=true)
    private Double unitPrice;
    @ApiModelProperty(required=false, hidden=true)
    private Double amount;
}
