package io.smarthealth.stock.purchase.domain;

import io.smarthealth.accounting.pricelist.domain.PriceBook;
import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.purchase.data.PurchaseOrderData;
import io.smarthealth.stock.purchase.data.PurchaseOrderItemData;
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
    
    public PurchaseOrderData toData() {
        PurchaseOrderData data = new PurchaseOrderData();
        data.setId(this.getId());
        data.setOrderNumber(this.getOrderNumber());
        if (this.getSupplier() != null) {
            data.setSupplierId(this.getSupplier().getId());
            data.setSupplierName(this.getSupplier().getSupplierName());
        }
        data.setTransactionDate(this.getTransactionDate());
        data.setRequiredDate(this.getRequiredDate());
        if (this.getAddress() != null) {
            data.setAddressId(this.getAddress().getId());
        }
        if (this.getContact() != null) {
            data.setContact(this.getContact().getFullName());
            data.setContactId(this.getContact().getId());
        }
        if (this.getStore() != null) {
            data.setStoreId(this.getId());
            data.setStore(this.getStore().getStoreName());
        }
        if (this.getPriceList() != null) {
            data.setStoreId(this.getPriceList().getId());
            data.setPriceList(this.getPriceList().getName());
        }
        data.setStatus(this.getStatus());
        data.setPurchaseAmount(this.getPurchaseAmount());
        data.setBilled(this.getBilled());
        data.setReceived(this.getReceived());
        data.setCreatedBy(this.getCreatedBy());

        List<PurchaseOrderItemData> list = this.getPurchaseOrderLines()
                .stream()
                .map(item -> PurchaseOrderItemData.map(item))
                .collect(Collectors.toList());

        data.setPurchaseOrderItems(list);

        return data;
    }
}
