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
 * @author Kent
 */
@Data  
@Entity
@Table(name = "patient_tests")
public class PatientTests extends ClinicalRecord {
    private String state;
    private String testName;
    private String code;
    private String clinicalDetails;
    private String priority;   
    
    @Setter(AccessLevel.NONE)
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade={javax.persistence.CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "request_Id", nullable = false)
    private List<results> results;
    
}
