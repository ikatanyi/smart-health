package io.smarthealth.stock.item.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.*;

/**
 *
 * @author Kelsas
 */
@Data
@Entity 
@Table(name = "stock_uom")
public class Uom extends Identifiable {

    public enum Category {
        Length, Surface, Time, Units, Volume, Weight
    }
    @Enumerated(EnumType.STRING)
    private Category category;

    private String name;

    @Column(length = 10)
    private String symbol;
    private boolean active;
}
