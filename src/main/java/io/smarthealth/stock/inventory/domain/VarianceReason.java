package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Identifiable; 
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *  Defined Reasons for Inventory Variance
 * 
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_variance_reason")
public class VarianceReason extends Identifiable{
    private String description;
}
