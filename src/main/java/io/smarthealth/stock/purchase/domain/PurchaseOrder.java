package io.smarthealth.stock.purchase.domain;

import io.smarthealth.accounting.pricebook.domain.PriceBook;
import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseOrderStatus;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.supplier.domain.Supplier;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "purchase_order")
public class PurchaseOrder extends Auditable {

    @NaturalId
    private String orderNumber; //PUR-ORD-2019-00001
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_purch_order_supplier_id"))
    private Supplier supplier;
    private LocalDate transactionDate;
    private LocalDate requiredDate;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_purch_order_address_id"))
    private Address address;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_purch_order_contact_id"))
    private Contact contact;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_purch_order_store_id"))
    private Store store;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_purch_order_pricelist_id"))
    private PriceBook priceList;
    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus status;
    //payment details that can be defined here that contains the terms and conditions for this and the rest will have to made
    @OneToMany(mappedBy = "purchaseOrder")
    private List<PurchaseOrderItem> purchaseOrderLines = new ArrayList<>();
    
      public void addOrderItem(PurchaseOrderItem item) {
        item.setPurchaseOrder(this);
        purchaseOrderLines.add(item);
    }

    public void addOrderItems(List<PurchaseOrderItem> items) {
        items.stream().map((item) -> {
            item.setPurchaseOrder(this);
            return item;
        }).forEachOrdered((bill) -> {
            purchaseOrderLines.add(bill);
        });
    }

}
