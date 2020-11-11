package io.smarthealth.organization.person.patient.domain;

import io.smarthealth.organization.person.domain.Person;
import io.smarthealth.organization.person.patient.data.enums.PatientStatus;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TermVector;

/**
 * The Patient
 *
 * @author Kelsas
 */
@Entity
@Data
@Indexed
@Table(name = "patient")
public class Patient extends Person {

    //Discount cards at the time of registration. 
    /**
     * Unique Health Identification Number - UHID
     */
//    @NaturalId
    @Column(length = 50, unique = true)
    @Field(termVector = TermVector.YES)
    private String patientNumber;   //HLC-PAT-2019-00002  | UHID-PT-2019-00002 

    @Column(length = 50)
    private String allergyStatus = "Unknown";

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PatientIdentifier> identifications;

    @Enumerated(EnumType.STRING)
    private PatientStatus status;

    @Column(length = 15)
    private String bloodType;

//    private Boolean isAlive = true;
    private String criticalInformation;
    private String basicNotes;

}
