package io.smarthealth.product.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "product_drug")
public class Drug extends Product{
    
    @ManyToOne
    private DrugClass drugClass;
    
    private String registrationNo;
    private String genericName;
    private String tradeName;
    private String strengthValue;
    private String unitOfStrength;
    private String dosageForm;
    private String routeOfAdministration;
    private String ATCCode1;
    private String packageType;
    private String packageSize;
    private String legalStatus; //OTC | Prescription

}
