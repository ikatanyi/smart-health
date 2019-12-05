/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.domain;

import io.smarthealth.clinical.procedure.domain.enumeration.Gender;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.item.domain.Item;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "radiology_tests")
@Inheritance(strategy = InheritanceType.JOINED)
public class ProcedureTest extends Identifiable{
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
    private Gender gender;  
}
