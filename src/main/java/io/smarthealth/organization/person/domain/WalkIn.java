/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.Data;
import org.hibernate.annotations.Formula;

/**
 *
 * @author Simon.waweru
 */
@Entity
@Data
public class WalkIn extends Auditable {

    private String firstName;
    private String secondName;
    private String surname;
    private String idNumber;
    private String phoneNo;
    @Column(nullable = false, unique = true)
    private String walkingIdentitificationNo;
    private String specialComments;

    private int age;

    @Formula(value = " concat(first_name, ' ', second_name, ' ', surname) ")
    private String fullName;
}
