/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pricebook.domain;

import io.smarthealth.stock.item.domain.Item;
import java.io.Serializable;
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

    private Double amount;

    private LocalDateTime createDate;

    public PriceBookItem(Item item, Double amount) {
        this.item = item;
        this.amount = amount;
        this.createDate = LocalDateTime.now();
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (!(o instanceof PriceBookItem)) {
//            return false;
//        }
//        PriceBookItem that = (PriceBookItem) o;
//        return Objects.equals(priceBook.getName(), that.priceBook.getName())
//                && Objects.equals(item.getItemName(), that.item.getItemName())
//                && Objects.equals(amount, that.amount)
//                && Objects.equals(createDate, that.createDate);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(priceBook.getName(), item.getItemName(), amount, createDate);
//    }
}
