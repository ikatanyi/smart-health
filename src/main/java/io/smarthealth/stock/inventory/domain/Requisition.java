package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.stock.item.domain.Item;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "stock_requisition")
public class Requisition extends Auditable {

    public enum Type {
        Purchase,
        Transfer,
        Issue
    }
  //TODO: on draft allow one to edit and submit to confirm and change state to pending
    // allow one to make - Purchase Order | Request for Quotation | Supplier Quotation
    public enum Status {
        Draft,
        Pending
    }

    private LocalDate transactionDate;
    private LocalDate requiredDate;
    private Department store;
    @NaturalId
    private String requestionNo; //RQ-2019-00002
    @Enumerated(EnumType.STRING)
    private Type type;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String requestedBy;
    //we need a status for this
    private String terms;

    @OneToMany(mappedBy = "requistion")
    private List<RequisitionItem> requistionLines = new ArrayList<>();
}
