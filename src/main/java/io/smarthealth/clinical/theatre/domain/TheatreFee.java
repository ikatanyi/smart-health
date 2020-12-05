/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.theatre.domain;

import io.smarthealth.clinical.theatre.domain.enumeration.FeeCategory;
import io.smarthealth.accounting.doctors.domain.DoctorItem;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "theatre_doctors_fee")
@Inheritance(strategy = InheritanceType.JOINED)
public class TheatreFee extends DoctorItem {

    private FeeCategory feeCategory;
}
