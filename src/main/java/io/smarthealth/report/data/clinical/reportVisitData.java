/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.data.clinical;

import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kennedy.ikatanyi
 */
@Data
public class reportVisitData {

    private String visitNumber;
    private String patientNumber;
    private String patientName;
    private LocalDate date;
    private String startDatetime="";
    private String stopDatetime="";
    private String consultation="0";
    private String procedure="0";
    private String radiology="0";
    private String triage="0";
    private String laboratory="0";
    private String pharmacy="0";
    private String other="0"; 
    private Double total=0.0;


}
