package io.smarthealth.financial.invoicing.patient.domain;

import io.smarthealth.organization.partner.insurance.domain.Payer;
import io.smarthealth.financial.accounting.domain.Period;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.domain.Partner;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

/**
 *  Patient Invoices
 * 
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_invoice")
public class Invoice extends Auditable {
    public enum Status{
        Draft,
        Cancelled,
        Final,
        Dispatched,
        Paid
    }
    @ManyToOne
    private Patient patient;
    
    @ManyToOne
    private Payer payer;  
    @ManyToOne
    private Period period;
    @NaturalId
    private String invoiceNumber;
    private LocalDate invoiceDate;
    @Column(length = 32)
    private String reference;
    private BigDecimal amount;
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToMany(mappedBy = "invoice")
    private List<InvoiceLine> invoiceLines=new ArrayList<>();
    
   // we should a claim processing table
    //track dispatch, allocations, returns
  
}
