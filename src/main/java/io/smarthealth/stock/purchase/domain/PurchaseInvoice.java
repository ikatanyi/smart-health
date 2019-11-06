package io.smarthealth.stock.purchase.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.supplier.domain.Supplier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "purchase_invoice")
public class PurchaseInvoice extends Auditable {

    public enum Status {
        Unpaid,
        Paid
    }
    @OneToOne
    private Supplier supplier;
    private String serialNumber; //ACC-PINV-2019-00001
    private LocalDate transactionDate;
    private LocalDateTime postingDatetime;
    private LocalDate dueDate;
    private Boolean paid;
    private Boolean isReturn; //debit note
    private String invoiceNo; //supplier invoice number
    private LocalDate invoiceDate; //supplier invoice date
    private BigDecimal invoiceAmount;
    @Enumerated(EnumType.STRING)
    private Status status;
}
