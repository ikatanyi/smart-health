package io.smarthealth.financial.accounting.domain;

import io.smarthealth.common.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *  Accounting Period
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_period")
public class Period extends Identifiable{
    @ManyToOne
    private FiscalYear fiscalyear;
}
