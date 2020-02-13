package io.smarthealth.debtor.claim.writeOff.data;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import io.smarthealth.debtor.claim.writeOff.domain.WriteOff;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class WriteOffData {  
    private Long id;
    private Long payerId;
    private String payer;
    private String scheme;
    private Long schemeId;
    private String comments;
    private String invoiceNo;
    private Double amount;
    
    
    
    public static WriteOff map(WriteOffData data){
        WriteOff writeOff = new WriteOff();
        writeOff.setComments(data.getComments());
        writeOff.setAmount(data.getAmount());
        return writeOff;
    }
}
