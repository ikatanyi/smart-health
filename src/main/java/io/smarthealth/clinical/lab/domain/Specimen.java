package io.smarthealth.clinical.lab.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Entity
@Data
@Table(name = "lab_specimen",uniqueConstraints = {
    @UniqueConstraint(columnNames = {"specimen"}, name="unique_specimen_name")})
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
