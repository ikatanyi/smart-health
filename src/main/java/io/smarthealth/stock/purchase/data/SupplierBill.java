/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.purchase.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kelsas
 */
@Data
public class SupplierBill {
    @javax.validation.constraints.NotNull(message = "Supplier is Required.")
    private Long supplierId;
    private String supplier;
    private List<SupplierBillItem> bills=new ArrayList<>();
}
