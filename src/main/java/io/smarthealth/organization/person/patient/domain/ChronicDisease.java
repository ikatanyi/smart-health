/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Simon.Waweru
 */
@Entity
@Data
@Table(name = "chronic_disease")
public class ChronicDisease extends Identifiable {
    private String code;
    private String name;
}
