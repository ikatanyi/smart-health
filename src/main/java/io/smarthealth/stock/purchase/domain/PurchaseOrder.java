package io.smarthealth.stock.purchase.domain;

import io.smarthealth.organization.contact.domain.Address;
import io.smarthealth.organization.contact.domain.Contact;
import io.smarthealth.accounting.payment.domain.PriceList;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.supplier.domain.Supplier;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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

    public enum Status {
        Draft,
        Approved,
        To_Receive_And_Bill
    }
    @NaturalId
    private String orderNumber; //PUR-ORD-2019-00001
    @ManyToOne
    private Supplier supplier;
    private LocalDate transactionDate;
    private LocalDate requiredDate;
    @OneToOne
    private Address address;
    @OneToOne
    private Contact contact;
    @OneToOne
    private Department store;
    @OneToOne
    private PriceList priceList;
    @Enumerated(EnumType.STRING)
    private Status status;

    //payment details that can be defined here that contains the terms and conditions for this and the rest will have to made
    @OneToMany(mappedBy = "purchaseOrder")
    private List<PurchaseOrderItem> purchaseOrderLines = new ArrayList<>();

}
