/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.invoice.data;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class MergeInvoice {
    private String invoiceNo;
    private String invoiceToMerge;
    private String reason;
}
