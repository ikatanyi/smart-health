/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import io.smarthealth.clinical.record.domain.ClinicalRecord;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data  
@Entity
@Table(name = "lab_test")
public class LabTest extends ClinicalRecord {
    private String state;
    private String testName;
    private String code;
    private String clinicalDetails;
    private String priority;   
    private String specimen;   
    private String specimenCollectionTime;   
    
    
    
    @Setter(AccessLevel.NONE)
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade={javax.persistence.CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "request_Id", nullable = false)
    private List<Results> results;
    
}
