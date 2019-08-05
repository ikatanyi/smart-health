package io.smarthealth.financial.account.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_transaction")
public class Transaction extends Identifiable {

    private String activity;
    private String referenceNo;
    private String description;
    private LocalDate documentDate;
    private LocalDateTime postingDate;
    private String paymentReference;
    @ManyToOne
    private FiscalYear fiscalYear;
    private Boolean reconciled; 
    @OneToMany(mappedBy = "transaction")
    private List<TransactionLine> transactionLines;

    //we can have a journal to represent the details for the transactions here
}
