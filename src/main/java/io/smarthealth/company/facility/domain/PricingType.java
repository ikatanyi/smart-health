/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.company.facility.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
public class PricingType extends Identifiable{
    private String description;
    
}
