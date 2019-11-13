package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.Uom;
import io.smarthealth.stock.stores.domain.Store;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Stock Entry
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_stock_movement")
public class StockMovement extends Auditable {
 
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_stock_movement_store_id"))
    private Store store;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_stock_movement_item_id"))
    private Item item;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_stock_movement_uom_id"))
    private Uom uom;
    private double receiving;
    private double issuing;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
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
}
