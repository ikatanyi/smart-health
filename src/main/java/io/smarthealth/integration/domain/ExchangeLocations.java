/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kent
 */
@Data  
@Entity
@Table(name = "exchange_locations")
public class ExchangeLocations extends Identifiable {
    private String slId;
    private String spId;
    private String locationDescription;
    private String groupPracticeNumber;
    private String countryCode;
}
