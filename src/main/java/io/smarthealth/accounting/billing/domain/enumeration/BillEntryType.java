/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.billing.domain.enumeration;

/**
 *
 * @author Kelsas
 */
public enum BillEntryType {
    Credit,
    Debit
}

/*

UPDATE patient_billing_item i, product_services p 
SET i.entry_type = case when (p.category = 'NHIF_Rebate' OR p.category ='Receipt' OR p.category = 'CoPay' ) then 'Credit' ELSE 'Debit' END  
WHERE p.id=i.item_id

*/