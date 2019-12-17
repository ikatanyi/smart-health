/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import io.smarthealth.clinical.lab.domain.enumeration.LabTestState;
import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
@Entity
public class PatientLabTestSpecimen extends Auditable {

    @Column(name = "collection_time", columnDefinition = "TIMESTAMP", nullable = false, unique = false)
    private LocalDateTime collectionTime;

    private String comments;

    @ManyToOne(cascade = CascadeType.ALL)
    private Specimen specimen;

    @ManyToOne(cascade = CascadeType.ALL)
    private PatientLabTest patientLabTest;    
    

}
