/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.data.clinical;

import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class OpData {
    private String ageGroup;
    private Integer newMale;
    private Integer newFemale;
    private Integer revMale;
    private Integer revFemale;
}
