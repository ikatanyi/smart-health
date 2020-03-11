package io.smarthealth.debtor.claim.creditNote.data;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class CreditNoteItemData { 
    @ApiModelProperty(required=false, hidden=true)
    private Long billItemid;
    @ApiModelProperty(required=false, hidden=true)
    private Long itemId;
    @ApiModelProperty(required=false, hidden=true)
    private String itemName;
    @ApiModelProperty(required=false, hidden=true)
    private Double amount;
}
