package io.smarthealth.organization.person.patient.domain;

import io.smarthealth.organization.person.domain.Person;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 * The Patient
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient")
public class Patient extends Person {

    //Discount cards at the time of registration. 
    /**
     * Unique Health Identification Number - UHID
     */
//    @NaturalId
    @Column(length = 50, unique = true)
    private String patientNumber;   //HLC-PAT-2019-00002  | UHID-PT-2019-00002 

    @Column(length = 50)
    private String allergyStatus = "Unknown";

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PatientIdentifier> identifications;

    @Column(length = 50)
    private String status;

    @Column(length = 15)
    private String bloodType;

    private boolean isAlive = false;

}
