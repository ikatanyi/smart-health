/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.purchase.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.item.domain.Item;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "supplier_credit_note_items")
public class PurchaseCreditNoteItem extends Identifiable {

    @ManyToOne
    private PurchaseCreditNote purchaseCreditNote;

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_credit_note_item_id"))
    @ManyToOne
    private Item item;
    private Double quantity;
    private BigDecimal rate;
    private BigDecimal tax;
    private BigDecimal amount;
}
