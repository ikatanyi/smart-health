/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kent
 */
@Data  
@Entity
@Table(name = "patient_results")
public class Results extends Identifiable {
    private Long id;
    private String testCode;
    private String testType;
    private String testName;
    private String normalRange;
    private String units;
    private String category;
    private String results;

}
