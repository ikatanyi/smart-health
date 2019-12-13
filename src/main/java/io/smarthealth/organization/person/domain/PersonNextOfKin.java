/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Simon.waweru
 */
@Entity
@Data
@Table(name = "person_next_of_kin")
public class PersonNextOfKin extends Identifiable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Person person;

    private String name;
    private String relationship;
    private String specialNote;

}
