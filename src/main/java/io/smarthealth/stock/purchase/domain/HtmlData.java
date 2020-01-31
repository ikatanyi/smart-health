/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.purchase.domain;

import io.smarthealth.stock.purchase.data.PurchaseOrderData;
import lombok.Value;

/**
 *
 * @author Kelsas
 */
@Value
public class HtmlData {

    private String tohtml;
    private PurchaseOrderData purchaseOrder;
}
