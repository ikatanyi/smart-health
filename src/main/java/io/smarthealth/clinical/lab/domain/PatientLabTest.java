/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import io.smarthealth.clinical.lab.domain.enumeration.LabTestState;
import io.smarthealth.infrastructure.domain.Identifiable;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * Patient Lab Request
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "patient_lab_test")
public class PatientLabTest extends Identifiable {

    @ManyToOne
    private LabTestType testType;
    private double testPrice;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private LabTestState status;
    //private String accessNo;

    @ManyToOne
    private PatientTestRegister patientTestRegister;

    private LocalDateTime specimenCollectionTime;

    @ManyToMany
    @JoinTable(name = "patient_lab_test_specimen")
    private List<Specimen> specimen;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = {javax.persistence.CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "request_id", nullable = false)
    private List<Results> results;

}
