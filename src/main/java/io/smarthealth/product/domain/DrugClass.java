package io.smarthealth.product.domain;

import io.smarthealth.infrastructure.domain.SetupMetadata;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *   Represents drug group like Antibiotics
 * @author Kelsas
 */
@Entity
@Table(name = "product_drug_class")
public class DrugClass extends SetupMetadata{
    
}
