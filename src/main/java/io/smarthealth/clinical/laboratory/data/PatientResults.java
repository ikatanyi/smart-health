/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.laboratory.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class PatientResults {

    private Long id;
    private String patientNo;
    private String patientName;
    private LocalDate visitDate;
    private String visitNumber;
    private String testName;
    private Long testId;
    private List<LabResultData> results = new ArrayList<>();

}
