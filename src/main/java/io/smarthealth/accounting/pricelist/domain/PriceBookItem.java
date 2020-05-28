/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pricelist.domain;

import io.smarthealth.stock.item.domain.Item;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author Kelsas
 */
@Data
@NoArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"item", "priceBook"})
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
    //determine the actual price for this based on the configurations

    public PriceList toPriceBookItemRate(PriceList priceList) {
        if (!Objects.equals(priceList.getItem().getId(), this.item.getId())) {
            return priceList;
        }
        priceList.setHasPriceBook(Boolean.TRUE);
        priceList.setPriceBookAmount(this.amount);
        
        return priceList;
    }
     @Override
    public String toString() {
        return "PriceBookItem [item=" + item.getItemName() + ", pricebook=" + priceBook.getName() + "]";
    }
}
