package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.company.facility.domain.Department;
import io.smarthealth.company.facility.domain.Employee;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.Uom;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *   Stock Entry 
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_movement")
public class StockMovement extends Auditable {

    public enum Purpose {
        Issue,
        Receipt,
        Transfer,
        Return
    }

    public enum Type {
        /**
         * Items received from Suppliers against Purchase Orders.
         */
        Purchase,
        /**
         * Items transferred from one Warehouse to another.
         */
        Stock_Entry,
        /**
         * Items dispensed to patient
         */
        Dispensed
    }
    // issued to details
    @ManyToOne
    private Department store;
    @ManyToOne
    private Item item;
    @OneToOne
    private Uom uom;
    private double receiving;
    private double issuing;
    private BigDecimal price;
    private BigDecimal total;
    @Enumerated(EnumType.STRING)
    private Type moveType;
    @Enumerated(EnumType.STRING)
    private Purpose purpose;
    private String transactionNo; //auto generated ST-2019-00002
    private String reference;
    private LocalDateTime transDatetime;
    @OneToOne
    private Employee transactingUser;

    /*
    Perpetual Inventory
        If perpetual inventory system is enabled, additional costs will be booked in "Expense Included In Valuation" account.
    Periodic
        Do not post stocks
    */
}
