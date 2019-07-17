package io.smarthealth.product.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.product.domain.UomCategory;
import io.smarthealth.infrastructure.domain.SetupMetadata;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
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

    @OneToOne
    private UomCategory category;
    private String name;
    private double rounding;
    @Column(length = 10)
    private String symbol;
    private double rate;
    private double factor;
    private boolean active;
}
