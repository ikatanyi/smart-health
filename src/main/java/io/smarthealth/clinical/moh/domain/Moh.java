package io.smarthealth.clinical.moh.domain;

import io.smarthealth.stock.item.domain.*;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_uom")
public class Moh extends Identifiable {

    public enum Category {
        morbidity,
    }
    
    @Enumerated(EnumType.STRING)
    private Category category;

    private String description;

    @Column(length = 10)
    private String code;
    private boolean active;
}
