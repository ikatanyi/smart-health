/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.data.accounts;

import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class InvoiceItemData {
    private String item;
    private Double amount;
    private Double balance;
    private Double quantity;
    private Double price;
    private Double discount;
    private Double taxes;
    private String servicePoint;
    private String billingDate;
}
