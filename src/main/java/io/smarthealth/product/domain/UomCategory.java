package io.smarthealth.product.domain;

import io.smarthealth.common.domain.SetupMetadata;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Kelsas
 */
@Entity
@Table(name = "product_uom_category")
public class UomCategory extends SetupMetadata{
  //[Length,Surface,Time,Units,Volume,Weight]
}
