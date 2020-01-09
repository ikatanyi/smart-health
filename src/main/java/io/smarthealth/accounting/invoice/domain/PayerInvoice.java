package io.smarthealth.accounting.invoice.domain;

import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payer_invoice")
@EntityListeners(AuditingEntityListener.class)
public class PayerInvoice implements Serializable {

    @EmbeddedId
    private PayerInvoiceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_payer_invoices_payer_id"))
    @MapsId("payerId")
    private Payer payer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_payer_invoices_scheme_id"))
    @MapsId("schemeId")
    private Scheme scheme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_payer_invoice_invoice_id"))
    @MapsId("invoiceId")
    private Invoice invoice;

    private String patientNumber;
    private String billNumber;
    private String memberName;
    private String memberNumber;
    private Double invoiceAmount;
    private LocalDate invoiceDate;
    private Double balance;
    private String status;
    private String preparedBy;

    @Version
    private Long version;
    @CreatedDate
    protected Instant createdOn;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    protected Instant lastModifiedOn;
    @LastModifiedBy
    private String lastModifiedBy;
    @Column(length = 38)
    private String companyId;
}
