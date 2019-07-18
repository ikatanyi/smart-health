package io.smarthealth.inventory.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.product.domain.Product;
import io.smarthealth.product.domain.Uom;
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
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_movement")
public class StockMovement extends Auditable {

    public enum TransactionType {
        Issuing,
        Receiving,
        Return_Inward,
        Return_Outward
    }
    // issued to details
    @ManyToOne
    private Department store;
    @ManyToOne
    private Product product;
    @OneToOne
    private Uom uom;
    private double receiving;
    private double issuing;
    private BigDecimal price;
    private BigDecimal total;
    private TransactionType transactionType;
    private String transactionNo;
    private String reference;
    private LocalDateTime transDatetime;
    private Employee transactingUser;

}
