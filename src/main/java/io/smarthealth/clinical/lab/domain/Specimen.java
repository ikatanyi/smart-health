package io.smarthealth.clinical.lab.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Entity
@Data
@Table(name = "lab_specimen")
public class Specimen extends Identifiable {

    private String specimen;
    private String abbreviation;

//    @OneToOne(mappedBy = "specimen",cascade = {javax.persistence.CascadeType.ALL}, orphanRemoval = true)
    @ManyToOne
    private Container container;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<LabTestType> labTest;

//    @ManyToOne
//    private PatientLabTestSpecimen patientLabTestSpecimen;
//
//    @ManyToMany(cascade = CascadeType.ALL/*, mappedBy = "specimen"*/)
//    private List<PatientLabTest> patientLabTest;
}
