package io.smarthealth.stock.purchase.domain;

import io.smarthealth.accounting.pricebook.domain.PriceBook;
import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseOrderStatus;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.supplier.domain.Supplier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "purchase_order")
public class PurchaseOrder extends Auditable {

//    @NaturalId
    private String orderNumber; //PUR-ORD-2019-00001
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_purch_order_supplier_id"))
    private Supplier supplier;
     private Boolean received;
    private Boolean billed;
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
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private List<PurchaseOrderItem> purchaseOrderLines;

    public PurchaseOrder() {
    }

    public PurchaseOrder(PurchaseOrder order, PurchaseOrderItem... orderItems) {
        this.address = order.getAddress();
        this.contact = order.getContact();
        this.orderNumber = order.getOrderNumber();
        this.priceList = order.getPriceList();
        this.requiredDate = order.getRequiredDate();
        this.status = order.getStatus();
        this.store = order.getStore();
        this.supplier = order.getSupplier();
        this.transactionDate = order.getTransactionDate();
        this.purchaseOrderLines = Stream.of(orderItems).collect(Collectors.toList());
        this.purchaseOrderLines.forEach(x -> x.setPurchaseOrder(this));
    }

    public void addOrderItem(PurchaseOrderItem item) {
        item.setPurchaseOrder(this);
        purchaseOrderLines.add(item);
    }

    public void addOrderItems(List<PurchaseOrderItem> items) {
        this.purchaseOrderLines = items;
        this.purchaseOrderLines.forEach(x -> x.setPurchaseOrder(this));
    }

    public BigDecimal getPurchaseAmount() {
        return this.purchaseOrderLines
                .stream()
                .map(x -> x.getAmount())
                .reduce(BigDecimal.ZERO, (x, y) -> x.add(y));
    }
}
