/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import io.smarthealth.clinical.lab.domain.enumeration.LabTestState;
import io.smarthealth.clinical.record.domain.ClinicalRecord;
import io.smarthealth.clinical.record.domain.DoctorRequest;
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
public class PatientLabTest extends ClinicalRecord {

    @Enumerated(EnumType.STRING)
    private LabTestState state;
    private String clinicalDetails;
    private String specimen;
    private LocalDateTime specimenCollectionTime;
    private String LabTestNumber;    
    @ManyToOne
    private DoctorRequest request;    
    @OneToOne
    private LabTestType testtype;

   // @Setter(AccessLevel.NONE)
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = {javax.persistence.CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "request_id", nullable = false)
    private List<Results> results;

//    public void setResults(List<Results> results) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

}
