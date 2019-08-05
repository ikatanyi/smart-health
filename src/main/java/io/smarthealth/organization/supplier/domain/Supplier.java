package io.smarthealth.organization.supplier.domain;
 
import io.smarthealth.organization.domain.Organization; 
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *  {@link  Organization} Supplier - Creditors
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "supplier")
public class Supplier extends Organization {
 
    @Enumerated(EnumType.STRING)
    private Type supplierType;
     
    @ManyToOne
    private SupplierGroup supplierGroup;
  
}
