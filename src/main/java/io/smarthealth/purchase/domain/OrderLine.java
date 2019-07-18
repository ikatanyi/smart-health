/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.purchase.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.product.domain.Product;
import io.smarthealth.product.domain.Uom;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "purchase_order_line")
public class OrderLine extends Identifiable{
    @ManyToOne
    private PurchaseOrder purchaseOrder;
    private Product product;
    private Uom uom;
    private double quantity;
    private BigDecimal price;
    private BigDecimal amount;
}
