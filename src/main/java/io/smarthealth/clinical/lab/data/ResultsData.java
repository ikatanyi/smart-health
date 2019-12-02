/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.lab.domain.Results;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author kent
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultsData {

    @ApiModelProperty(hidden = true, required = false)
    private String analyteName;

    @ApiModelProperty(hidden = true, required = false)
    private Long resultId;

    @ApiModelProperty(hidden = true, required = false)
    private Long patientTestId;

    @ApiModelProperty(hidden = true, required = false)
    private Long analyteId;

    @ApiModelProperty(hidden = true, required = false)
    private String testName;

    private String upperRange;
    private String lowerRange;

    @ApiModelProperty(required = true)
    private String unit;

    @ApiModelProperty(required = true)
    private String resultValue;

    @ApiModelProperty(required = true)
    private String status;

    private String comments;

    public static Results map(ResultsData d) {
        Results e = new Results();
        e.setComments(d.getComments());
        e.setLowerRange(d.getLowerRange());
        e.setResultValue(d.getResultValue());
        e.setUnit(d.getUnit());
        e.setUpperRange(d.getUpperRange());
        e.setStatus(d.getStatus());
        return e;
    }

    public static ResultsData map(Results e) {
        ResultsData d = new ResultsData();
        d.setAnalyteName(e.getAnalyte().getAnalyteName());
        d.setAnalyteId(e.getAnalyte().getId());
        d.setComments(e.getComments());
        d.setLowerRange(e.getLowerRange());
        d.setPatientTestId(e.getPatientLabTest().getId());
        d.setResultId(e.getId());
        d.setResultValue(e.getResultValue());
        d.setStatus(e.getStatus());
        d.setTestName(e.getPatientLabTest().getTestType().getTestType());
        d.setUnit(e.getUnit());
        d.setUpperRange(e.getUpperRange());
        return d;
    }

    public static List<ResultsData> map(List<Results> entities) {
        List<ResultsData> data = new ArrayList<>();
        for (Results e : entities) {
            ResultsData d = new ResultsData();
            d.setAnalyteName(e.getAnalyte().getAnalyteName());
            d.setAnalyteId(e.getAnalyte().getId());
            d.setComments(e.getComments());
            d.setLowerRange(e.getLowerRange());
            d.setPatientTestId(e.getPatientLabTest().getId());
            d.setResultId(e.getId());
            d.setResultValue(e.getResultValue());
            d.setStatus(e.getStatus());
            d.setTestName(e.getPatientLabTest().getTestType().getTestType());
            d.setUnit(e.getUnit());
            d.setUpperRange(e.getUpperRange());
            data.add(d);
        }
        return data;
    }
}
