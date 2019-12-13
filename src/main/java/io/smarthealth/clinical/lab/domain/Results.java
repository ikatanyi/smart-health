/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

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
    private Double upperRange;
    private Double lowerRange;
    private String units;
    private String category;
    private String results;
    private String comments;

}
