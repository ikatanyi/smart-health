package io.smarthealth.organization.partner.supplier.domain;
 
import io.smarthealth.organization.partner.domain.Partner;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "supplier")
public class Supplier extends Partner {
 
    @Enumerated(EnumType.STRING)
    private Type supplierType;
     
    @ManyToOne
    private SupplierGroup supplierGroup;
     
}
