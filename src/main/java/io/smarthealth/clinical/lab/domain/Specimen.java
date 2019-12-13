package io.smarthealth.clinical.lab.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Entity
@Data
//@NamedQuery(name="tets",query = "SELECT e FROM Analyte e WHERE e.testType = :testType AND e.sex = :gender AND :age BETWEEN e.startAge and e.endAge ")
@Table(name = "lab_specimen")
public class Specimen extends Identifiable {

    @ManyToOne
    private LabTestType testtype;

    private String specimen;
    private String abbreviation; 
    
//    @OneToOne(mappedBy = "specimen",cascade = {javax.persistence.CascadeType.ALL}, orphanRemoval = true)
    @ManyToOne
    private Container container;

//    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(
//            name = "lab_test_specimen",
//            joinColumns = @JoinColumn(name = "test_id"),
//            inverseJoinColumns = @JoinColumn(name = "specimen_id"))
//    private List<Testtype> testType;
}
