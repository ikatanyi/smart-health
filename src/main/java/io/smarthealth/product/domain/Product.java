package io.smarthealth.product.domain;

import io.smarthealth.financial.accounting.domain.Account;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.partner.supplier.domain.Supplier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Product represents the Products & Services in a {@link Facility} Products are
 * the basic entity for creating invoices. Therefore every product needs a list
 * price, a cost price and a unit of measure (UOM) for calculating costs.
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "product_product")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Product extends Identifiable {

    /**
     * Product Category
     */
    public enum Type {
        /**
         * Quantities in and out are tracked
         */
        Stock,
        /**
         * Quantities are not tracked
         */
        Consumable,
        /**
         * Services that are bought or sold
         */
        Service
    }

    /**
     * Drugs should be named following format DRUG | STRENGTH | FORM
     */
    private String description;
    private String code;
    private String sku;  //Stock Keeping Unit
    @Enumerated(EnumType.STRING)
    private Type type;
    @OneToOne
    private ProductCategory category;
    private BigDecimal costPrice;
    private BigDecimal salesPrice;
    private Boolean consumable; //can it be sold
    private Boolean purchasable; //can it be bought
    @OneToOne
    private Uom uom;
    private boolean active;
    private LocalDate dateCreated;

    /**
     * Suppliers list linked in supplying this {@link  Product}
     */
    @ManyToMany(mappedBy = "products")
    private Set<Supplier> suppliers = new HashSet<>();
    /**
     * Inventory Reordering Rules
     */
    @OneToOne
    private ReorderRule reorderRule;

    @OneToOne
    private Account incomeAccount;
    @OneToOne
    private Account expenseAccount;

    //Todo :: Method of costing the price and it's price list 
    //this should be linked to a gl account
    //this product may be linked to many gla
    ///
    //need to know the debit and credit account that belongs to this account for me to make it
}
