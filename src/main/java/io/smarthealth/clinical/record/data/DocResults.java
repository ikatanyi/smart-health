/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocResults {

    public enum Type {
        Laboratory,
        Radiology
    }
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime date;
    private String visitNumber;
    private String patientNo;
    private String patientName;
    private Type requestType;
    private Long resultID;

    //here I need to select results from lab and those from radiology
}
