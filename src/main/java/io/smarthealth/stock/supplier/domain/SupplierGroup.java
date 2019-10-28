package io.smarthealth.stock.supplier.domain;

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
@Table(name = "supplier_group")
public class SupplierGroup extends Identifiable{
    private String description;
}
