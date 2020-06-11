/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.data;

import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class DocResults {

    private LocalDate date;
    private String visitNumber;
    private String patientNo;
    private String patientName;
    private DoctorRequestData.RequestType requestType;
    private String notes;

}
