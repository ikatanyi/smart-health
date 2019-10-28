/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.organization.payer.domain.Payer;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
@Entity
@Table(name = "patient_insurance")
public class InsuranceInformation extends Identifiable {

    @ManyToOne
    @JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "fk_insurance_patient_id"))
    private Patient patient;

    @ManyToOne
    private Payer payer;

    private String subscriberName;
    private String policyNo;
    private String benefitName;
    private boolean insuranceActive;// (false/true)
    private String specialComments;

}
