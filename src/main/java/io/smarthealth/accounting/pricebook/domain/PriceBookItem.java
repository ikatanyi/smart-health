/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pricebook.domain;

import io.smarthealth.stock.item.domain.Item;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@NoArgsConstructor
@Entity
@EqualsAndHashCode(exclude={"item","priceBook"})
public class PriceBookItem implements Serializable {

    @Id 
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_pricebook_item_book_id"))
    private PriceBook priceBook;

    @Id 
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_pricebook_item_item_id"))
    private Item item;

    private BigDecimal amount;

    private LocalDateTime createDate;

    public PriceBookItem(Item item, BigDecimal amount) {
        this.item = item;
        this.amount = amount;
        this.createDate = LocalDateTime.now();
    }
 
}
