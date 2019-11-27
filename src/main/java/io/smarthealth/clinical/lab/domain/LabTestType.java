package io.smarthealth.clinical.lab.domain;

import io.smarthealth.clinical.lab.data.LabTestTypeData;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.item.domain.Item;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Defines Lab Test service for the laboratory
 *
 * @author Kennedy.Imbenzi
 */
@Entity
@Data
@Table(name = "lab_test_type")
public class LabTestType extends Identifiable {

    @OneToMany(mappedBy = "testType")
    private List<PatientLabTest> patientLabTests;

    //private String serviceCode;
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

    @OneToMany(mappedBy = "testType", cascade = {javax.persistence.CascadeType.ALL}, orphanRemoval = true)
    private List<Analyte> analytes = new ArrayList<>();

    @ManyToOne
    private Item itemService;

//    @OneToMany(mappedBy = "testtype")
//    private List<Specimen> specimens = new ArrayList<>();
    @OneToOne
    private Discipline discipline;

//    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(name = "lab_specimen")
    @ManyToMany
    @JoinTable(name = "lab_test_specimen")
    private List<Specimen> specimen;

//    public void addSpecimen(Specimen specimen) {
//        specimen.setTestType(this);
//        specimens.add(specimen);
//    }
}
