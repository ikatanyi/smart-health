package io.smarthealth.stock.item.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "product_uom")
public class Uom extends Identifiable {

    public enum Category {
        Length, Surface, Time, Units, Volume, Weight
    } 
    @Enumerated(EnumType.STRING)
    private Category category;
    
    private String name;
    private double rounding;
    @Column(length = 10)
    private String symbol;
    private double rate;
    private double factor;
    private boolean active;
}
