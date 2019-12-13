package io.smarthealth.accounting.taxes.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "config_taxes")
public class Tax extends Identifiable {

    private String taxName;
    private double rate;
    private Boolean active;
}
