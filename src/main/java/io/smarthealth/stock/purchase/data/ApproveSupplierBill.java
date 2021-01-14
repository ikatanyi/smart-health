/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.purchase.data;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class ApproveSupplierBill {

    private Long billId;

    private String invoiceNumber;
    @NotNull(message = "Supplier is Required.")
    private Long supplierId;

}
