package io.smarthealth.debtor.claim.creditNote.data;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class CreditNoteItemData {  
    private Long id;
    private Long billItemid;
    private Long itemId;
    private Double amount;
}
