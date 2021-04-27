package io.smarthealth.stock.inventory.domain;

import io.smarthealth.clinical.pharmacy.domain.DispensedDrug;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.inventory.data.StockEntryData;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.supplier.domain.Supplier;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Stock Entry
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_inventory_entries")
public class StockEntry extends Auditable {
    public enum Status{
        Active,
        Received,
        Deleted
    }
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
    private String costCenter;
    private String issuedTo;
    private LocalDate transactionDate;

    @Enumerated(EnumType.STRING)
    private MovementType moveType;

    @Enumerated(EnumType.STRING)
    private MovementPurpose purpose;

    private LocalDate expiryDate;
    private String batchNo;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_stock_destination_store_id"))
    private Store destinationStore;
    private Double cachedQuantity;
    private String notes;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime receivedAt;
    private BigDecimal discount;
    private BigDecimal tax;

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
        if(this.destinationStore!=null){
            data.setDestinationStoreId(this.getDestinationStore().getId());
            data.setDestinationStore(this.getDestinationStore().getStoreName());
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
        data.setTransactionDate(this.getTransactionDate());
        data.setMoveType(this.getMoveType());
        data.setPurpose(this.getPurpose());
        data.setCostCenter(this.getCostCenter());
        data.setCreatedBy(this.getCreatedBy());
        data.setStatus(this.getStatus());
        data.setDiscount(this.getDiscount() !=null ? this.getDiscount() : BigDecimal.ZERO);
        data.setTax(this.getTax() != null? this.getTax() : BigDecimal.ZERO);

        data.setDateCreated(this.getTransactionDate());
        data.setLastUpdated(this.getReceivedAt());

        data.setCostPrice(this.getItem().getRate());
        data.setFormattedQuantity(this.getQuantity()* -1);
        data.setFormattedTotal(this.getItem().getRate().multiply(BigDecimal.valueOf(this.getQuantity()* -1)));
        data.setFixedQuantity(this.getQuantity());

        data.setTotalExclusive(data.getFormattedTotal().subtract(data.getDiscount()));
        data.setTotalAmount(data.getTotalExclusive().add(data.getTax()));

        data.setTotalExclusive(data.getFormattedTotal().subtract(data.getDiscount()));
        data.setTotalAmount(data.getTotalExclusive().add(data.getTax()));

        return data;
    }

    public static StockEntry create(DispensedDrug drug) {
        Item item = drug.getDrug();
        Store store = drug.getStore();

        BigDecimal amt = BigDecimal.valueOf(drug.getAmount());
        BigDecimal price = BigDecimal.valueOf(drug.getPrice());

        StockEntry stock = new StockEntry();
        stock.setAmount(amt);
        stock.setQuantity(drug.getQtyIssued()*-1);
        stock.setItem(item);
        stock.setMoveType(MovementType.Dispensed);
        stock.setPrice(price);
        stock.setPurpose(MovementPurpose.Issue);

        if (drug.getWalkinFlag()) {
            stock.setReferenceNumber(drug.getOtherReference());
        } else {
            stock.setReferenceNumber(drug.getPatient().getPatientNumber());
        }
        stock.setIssuedTo(drug.getOtherReference());
        stock.setStore(store);
        stock.setTransactionDate(drug.getDispensedDate());
        stock.setTransactionNumber(drug.getTransactionId());
        stock.setUnit(drug.getUnits());
        stock.setBatchNo(drug.getBatchNumber());
        stock.setDiscount(BigDecimal.valueOf(drug.getDiscount()));
        stock.setTax(BigDecimal.valueOf(drug.getTaxes()));
        return stock;
    }
    public LocalDateTime getLastUpdatedDateTime(){
        return LocalDateTime.ofInstant(getLastModifiedOn(), ZoneOffset.systemDefault());
    }
}
