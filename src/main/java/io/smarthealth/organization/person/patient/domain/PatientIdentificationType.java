/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author simz
 */
@Entity
@Data
public class PatientIdentificationType extends Identifiable {

    @NotNull
    @Column(name = "identification_name", unique = true)
    private String identificationName;
}
