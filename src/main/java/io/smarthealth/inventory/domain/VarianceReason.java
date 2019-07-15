package io.smarthealth.inventory.domain;

import io.smarthealth.common.domain.SetupMetadata;
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
@Table(name = "inventory_variance_reason")
public class VarianceReason extends SetupMetadata{
    
}
