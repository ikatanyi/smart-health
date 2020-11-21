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
public class Moh706LabData {
    private String testName;    
    private Integer male = 0;
    private Integer female = 0;
    private Integer under5 = 0;
    private Integer over5 = 0;
    private Integer total = 0;
}
