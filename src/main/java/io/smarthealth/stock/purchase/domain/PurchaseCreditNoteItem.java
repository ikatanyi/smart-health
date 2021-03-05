package io.smarthealth.stock.purchase.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.purchase.data.PurchaseCreditNoteItemData;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "supplier_credit_note_item")
public class PurchaseCreditNoteItem extends Auditable{
    @OneToOne
    private Item item;
    private Double quantity;
    private BigDecimal rate;
    private BigDecimal tax;
    private BigDecimal amount;
    
    public PurchaseCreditNoteItemData toData(){
        PurchaseCreditNoteItemData data = new PurchaseCreditNoteItemData();
        data.setAmount(this.getAmount());
        data.setItemId(this.getId());
        data.setQuantity(this.getQuantity());
        if(this.getItem()!=null){
           data.setItem(this.getItem().getItemName());
           data.setRate(this.getRate());
           data.setTax(this.getTax());
        }
        return data;
    }

}
