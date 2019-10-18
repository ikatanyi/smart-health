package io.smarthealth.accounting.taxes.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_taxes")
public class Tax extends Identifiable{
    private String taxName;
    private double rate;
}
