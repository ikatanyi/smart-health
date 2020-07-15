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
 * @author Kennedy.Imbenzi
 */
@Data
public class specimenLabelData {
    private String patientName;
    private LocalDate dateOfBitrh;
    private String specimenCode;
    private String specimenName;
    private String requestedBy;
}
