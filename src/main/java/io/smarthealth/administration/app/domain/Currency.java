package io.smarthealth.administration.app.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.Entity;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
public class Currency extends Identifiable {

    private String code;
    private String symbol;
    private String name;
    private Integer decimalPlaces;
    private String format;
    private boolean active;
}
