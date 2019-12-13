package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.inventory.domain.enumeration.RequisitionStatus;
import io.smarthealth.stock.inventory.domain.enumeration.RequisitionType;
import io.smarthealth.stock.stores.domain.Store;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_requisitions")
public class Requisition extends Auditable {

    //TODO: on draft allow one to edit and submit to confirm and change state to pending
    // allow one to make - Purchase Order | Request for Quotation | Supplier Quotation
    private LocalDate transactionDate;
    private LocalDate requiredDate;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_requisition_store_id"))
    private Store store;
    private String requestionNumber; //RQ-2019-00002
    @Enumerated(EnumType.STRING)
    private RequisitionType type;
    @Enumerated(EnumType.STRING)
    private RequisitionStatus status;
    private String requestedBy;
    //we need a status for this
    private String terms;

    @OneToMany(mappedBy = "requistion")
    private List<RequisitionItem> requistionLines = new ArrayList<>();
    
    public void addRequsitionItem(RequisitionItem item) {
        item.setRequistion(this);
        requistionLines.add(item);
    }

    public void addRequsitionItems(List<RequisitionItem> items) {
        items.stream().map((item) -> {
            item.setRequistion(this);
            return item;
        }).forEachOrdered((bill) -> {
            requistionLines.add(bill);
        });
    }
}
