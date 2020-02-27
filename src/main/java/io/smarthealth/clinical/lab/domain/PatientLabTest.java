/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import io.smarthealth.clinical.lab.domain.enumeration.LabTestState;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.infrastructure.domain.Identifiable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 * Patient Lab Request
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "patient_lab_test")
public class PatientLabTest extends Auditable {

    @ManyToOne
    private LabTestType testType;
    private Double testPrice;
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private LabTestState status;

    @ManyToOne
    private LabRegister patientTestRegister;
    @OneToMany(mappedBy = "patientLabTest", cascade = {javax.persistence.CascadeType.ALL})
    private List<PatientLabTestSpecimen> patientLabTestSpecimens;

    @OneToMany(mappedBy = "patientLabTest", cascade = {javax.persistence.CascadeType.ALL})
    private List<Results> results;

}
