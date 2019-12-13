/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.data;

import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class AnalyteData {
    public enum Gender {
        Male,
        Female,
        Both
    }   
    private Long id;
    private Long testTypeId;
    private String testCode;
    private String testName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String startAge;
    private String category;
    private String endAge;
    private Double lowerRange;
    private Double upperRange;
    private String units;
    private String description;   
    
    
}
