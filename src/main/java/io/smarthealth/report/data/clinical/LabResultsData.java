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
public class LabResultsData {
    private String testName;
    private String upperRange;
    private String lowerRange;
    private String resultValue;
    private String unit;
    private String status;
    private String comments;
}
