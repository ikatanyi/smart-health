package io.smarthealth.financial.account.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_fiscal_year")
public class FiscalYear extends Auditable{
    @Column(length = 16)
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;
}
