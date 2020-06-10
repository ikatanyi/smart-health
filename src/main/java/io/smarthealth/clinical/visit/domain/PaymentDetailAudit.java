package io.smarthealth.clinical.visit.domain;

import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "patient_visit_payment_details_audit")
public class PaymentDetailAudit extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_payment_details_audit_visit_id"))
    private Visit visit;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_payment_details_audit_payer_id"))
    private Payer payer;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_payment_details_audit_scheme_id"))
    private Scheme scheme;

    private LocalDateTime changeDate;

    private String reason;
    private String memberName;
    private String policyNo;
    private String relation;
}
