package io.smarthealth.payer.domain;

import io.smarthealth.organization.domain.Partner;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *  Debtors/Insurances
 * @author Kelsas
 */
@Entity
@Table(name = "payer_insurance")
public class Payer extends Partner{
    //the Insurance payers
}
