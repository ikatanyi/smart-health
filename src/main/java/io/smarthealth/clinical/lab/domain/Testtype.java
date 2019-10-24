package io.smarthealth.clinical.lab.domain;

import io.smarthealth.clinical.lab.data.LabTestTypeData;
import io.smarthealth.infrastructure.domain.Identifiable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Entity
@Data
@Table(name = "test_type")
public class Testtype extends Identifiable {
    private String serviceCode;
    private String testType; //government classifications
    private Boolean consent; 
    private Boolean withRef; 
    private Boolean refOut; 
    private Long duration;
    private String durationDesc;
    private String notes;
    private Boolean supervisorConfirmation;
    @Enumerated(EnumType.STRING)
    private LabTestTypeData.Gender gender;    
    
    @OneToMany(mappedBy = "testType",cascade = {javax.persistence.CascadeType.ALL}, orphanRemoval = true)
    private List<Analyte> analytes = new ArrayList<>();
    
    @ManyToMany(mappedBy = "testType")
    private List<Specimen> specimens = new ArrayList<>();
    
    @OneToOne
    private Discipline discipline;
    
}
