/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author simz
 */
@Entity
@Data
@Table(name = "allergy_type")
public class AllergyType extends Identifiable {

    private String code;
    private String name;

}
