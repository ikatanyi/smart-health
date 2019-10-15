/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.data;

import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class AnalyteData {
    private Long id;
    private String testCode;
    private String testName;
    private String gender;
    private String startAge;
    private String category;
    private String endAge;
    private Double lowerRange;
    private Double upperRange;
    private String units;
    private String description;   
}
