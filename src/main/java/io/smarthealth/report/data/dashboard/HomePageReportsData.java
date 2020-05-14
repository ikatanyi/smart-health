/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.data.dashboard;

import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class HomePageReportsData {

    private Long patientCount;
    private Long visitCount;
    private Long appointmentCount;
    
    
}
