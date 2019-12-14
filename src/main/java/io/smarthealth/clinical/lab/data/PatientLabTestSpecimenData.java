/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.smarthealth.clinical.lab.domain.PatientLabTestSpecimen;
import io.smarthealth.infrastructure.lang.Constants;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) 	//  ignore all null fields
public class PatientLabTestSpecimenData {

    private Long specimenId;

    @ApiModelProperty(required = false, hidden = true)
    private String specimenName;

    @ApiModelProperty(required = false, hidden = true)
    private String labelNo;

    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime collectionTime;

    private String comments;

    private Long patientLabtestId;

    @ApiModelProperty(required = false, hidden = true)
    private String testName;

    public static PatientLabTestSpecimen map(PatientLabTestSpecimenData d) {
        PatientLabTestSpecimen s = new PatientLabTestSpecimen();
        s.setCollectionTime(d.getCollectionTime());
        s.setComments(d.getComments());
        return s;
    }

    public static PatientLabTestSpecimenData map(PatientLabTestSpecimen s) {
        PatientLabTestSpecimenData d = new PatientLabTestSpecimenData();
        d.setCollectionTime(s.getCollectionTime());
        d.setComments(s.getComments());
        d.setSpecimenId(s.getSpecimen().getId());
        d.setSpecimenName(s.getSpecimen().getSpecimen());
        d.setPatientLabtestId(s.getPatientLabTest().getId());
        d.setTestName(s.getPatientLabTest().getTestType().getTestType());
        return d;
    }

    public static List<PatientLabTestSpecimenData> map(List<PatientLabTestSpecimen> specimens) {
        List<PatientLabTestSpecimenData> sd = new ArrayList<>();
        for (PatientLabTestSpecimen s : specimens) {
            PatientLabTestSpecimenData d = new PatientLabTestSpecimenData();
            d.setCollectionTime(s.getCollectionTime());
            d.setComments(s.getComments());
            d.setSpecimenId(s.getSpecimen().getId());
            d.setSpecimenName(s.getSpecimen().getSpecimen());
            d.setPatientLabtestId(s.getPatientLabTest().getId());
            d.setTestName(s.getPatientLabTest().getTestType().getTestType());
            sd.add(d);
        }
        return sd;
    }
}
