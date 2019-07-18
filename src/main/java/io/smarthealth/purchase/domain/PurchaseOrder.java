/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.purchase.domain;

import io.smarthealth.financial.accounting.domain.Currency;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.partner.supplier.domain.Supplier;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
public class PurchaseOrder extends Auditable {

    public enum Status {
        Draft,
        Approved,
        Received
    }
   public enum Category{
       Normal,
       Urgent,
       Emergency
   }
    @OneToOne
    private Supplier supplier;
    private LocalDate orderDate;
    private LocalDate dueDate;
    @NaturalId
    private String orderNumber;
    @OneToOne
    private Currency currency;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Category category;
     @OneToMany(mappedBy = "purchaseOrder")
    private List<OrderLine> orderLines;
}
