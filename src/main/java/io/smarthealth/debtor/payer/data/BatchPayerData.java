/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.payer.data;

import lombok.Data;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Data
public class BatchPayerData {

    private String payerType;
    private String payerName;
    private String legalName;
    private String taxNumber;
    private String payerCode;
    private String website;
    private boolean insurance;
    private String schemeCode;
    private String schemeName;
    private String cover;
    private String discountMethod;
    private Double discountValue;
    private String coPayType;
    private Double coPayValue;
    private Boolean status;
    private Boolean smartEnabled;
    private String ledgerAccountCode;
    private String bankBranchCode;
    private String bankAccountNumber;
    private String paymentTermName;
    private String priceBookName;
    private String primaryContact;
    
}
