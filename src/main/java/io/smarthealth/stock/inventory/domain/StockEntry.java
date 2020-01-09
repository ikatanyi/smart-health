package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.inventory.data.StockEntryData;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import lombok.Data;

/**
 * Stock Entry
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_inventory_entries")
public class StockEntry extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_stock_entry_store_id"))
    private Store store;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_stock_entry_item_id"))
    private Item item;

    private Double quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private String unit;
    private String referenceNumber; //ref LPO,supplier, patient no
    private String deliveryNumber; //GRN| transaction reference
    private String transactionNumber; //auto generated ST-2019-00002
    private String journalNumber;

    private LocalDate transactionDate;

    @Enumerated(EnumType.STRING)
    private MovementType moveType;

    @Enumerated(EnumType.STRING)
    private MovementPurpose purpose;

    /*
    Perpetual Inventory
        If perpetual inventory system is enabled, additional costs will be booked in "Expense Included In Valuation" account.
    Periodic
        Do not post stocks
     */
    public StockEntryData toData() {
        StockEntryData data = new StockEntryData();
        data.setId(this.getId());
        if (this.getStore() != null) {
            data.setStoreId(this.getStore().getId());
            data.setStore(this.getStore().getStoreName());
        }
        if (this.getItem() != null) {
            data.setItemId(this.getItem().getId());
            data.setItemCode(this.getItem().getItemCode());
            data.setItem(this.getItem().getItemName());
        }

        data.setUnit(this.getUnit());
        data.setQuantity(this.getQuantity());
        data.setPrice(this.getPrice());
        data.setAmount(this.getAmount());
        data.setReferenceNumber(this.getReferenceNumber());
        data.setDeliveryNumber(this.getDeliveryNumber());
        data.setTransactionNumber(this.getTransactionNumber());
        data.setJournalNumber(this.getJournalNumber());
        data.setTransactionDate(this.getTransactionDate());
        data.setMoveType(this.getMoveType());
        data.setPurpose(this.getPurpose());

        return data;
    }
}
