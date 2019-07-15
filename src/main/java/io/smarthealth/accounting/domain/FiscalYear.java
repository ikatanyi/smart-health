package io.smarthealth.accounting.domain;

import io.smarthealth.common.domain.Identifiable;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_fiscal_year")
public class FiscalYear extends Identifiable {

    @Column(length = 16)
    private String name;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Boolean closed;
    
    @OneToMany(mappedBy = "fiscalyear")
    private List<Period> periods;
    
}
