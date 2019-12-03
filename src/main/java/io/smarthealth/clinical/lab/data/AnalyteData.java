/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

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
    private String analyteCode;
    private String analyteName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private int startAge;
    private String category;
    private int endAge;
    private Double lowerRange;
    private Double upperRange;
    private String units;
    private String description;

}
