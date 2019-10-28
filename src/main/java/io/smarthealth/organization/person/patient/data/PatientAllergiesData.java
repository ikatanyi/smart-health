/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import static io.smarthealth.infrastructure.lang.Constants.DATE_TIME_PATTERN;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class PatientAllergiesData {

    private Long id;
    private PatientData patientData;
    private String patientNumber;
    private String allergyType;
    private AllergyTypeData allergyTypeData;
    private String reaction;
    private String severity; //Critical | Mild | Danger
    private String notes;
    private String status;
    private String allergen;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime observedDate;
}
