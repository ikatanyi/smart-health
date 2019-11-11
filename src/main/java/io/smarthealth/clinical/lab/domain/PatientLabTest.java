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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "patient_lab_test")
public class PatientLabTest extends Identifiable {

    @ManyToOne
    private PatientTestRegister patientTestRegister;

    @Enumerated(EnumType.STRING)
    private LabTestState state;    
    private LocalDateTime specimenCollectionTime;    
    private LabTestType testtype;
    @OneToOne
    private Specimen specimen;
   // @Setter(AccessLevel.NONE)
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = {javax.persistence.CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "request_id", nullable = false)
    private List<Results> results;
    

//    public void setResults(List<Results> results) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

}
