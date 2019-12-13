/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;

import io.smarthealth.clinical.lab.data.LabTestTypeData;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.item.domain.Item;
import lombok.Data;

import javax.persistence.*;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "radiology_tests")
@Inheritance(strategy = InheritanceType.JOINED)
public class RadiologyTest extends Identifiable{
    private String scanName; //government classifications
    private Boolean consent; 
    private Boolean withRef; 
    private Boolean refOut; 
    private Boolean status; 
    private String notes;
    @OneToOne
    private Item item;
    private Boolean supervisorConfirmation;
    @Enumerated(EnumType.STRING)
    private LabTestTypeData.Gender gender;  
}
