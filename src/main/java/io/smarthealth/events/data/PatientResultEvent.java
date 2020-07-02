/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.events.data;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author Kelsas
 */
@Data
public class PatientResultEvent extends ApplicationEvent {

    private String userID;
    private String patientNo;
    private String patientName;
    //results that are read to be added here
 
    public PatientResultEvent(String userID, String patientNo, String patientName, Object source) {
        super(source);
        this.userID = userID;
        this.patientNo = patientNo;
        this.patientName = patientName;
    }

}
