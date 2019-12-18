package io.smarthealth.administration.app.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity; 
import lombok.Data;

/**
 *
 * @author Kelsas
 */ 
@Data
@Entity 
public class Currency extends Identifiable {

    private String code;
    private String symbol;
    private String name;
    private Integer decimalPlaces;
    private String format;
    private boolean active;
}
