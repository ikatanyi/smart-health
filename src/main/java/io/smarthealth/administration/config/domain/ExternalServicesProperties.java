/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.config.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "c_external_service_properties")
public class ExternalServicesProperties extends Identifiable {

    @EmbeddedId
    ExternalServicePropertiesPK externalServicePropertiesPK;

    @Column(name = "value", length = 250)
    private String value;
}
