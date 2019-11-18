/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.lab.domain.Results;
import lombok.Data;

/**
 *
 * @author kent
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultsData {
    private Long id;
    private String testCode;
    private String testType;
    private String testName;
    private Double upperRange;
    private Double lowerRange;
    private String units;
    private String category;
    private String results;
    private String comments;
    
    public static Results map(ResultsData resultdata) {
        Results entity = new Results();
        entity.setId(resultdata.getId());
        entity.setTestCode(resultdata.getTestCode());
        entity.setTestName(resultdata.getTestName());
        entity.setTestType(resultdata.getTestType());
        entity.setCategory(resultdata.getCategory());
        entity.setLowerRange(resultdata.getLowerRange());
        entity.setUpperRange(resultdata.getUpperRange());
        entity.setResults(resultdata.getResults());
        entity.setUnits(resultdata.getUnits());
        entity.setComments(resultdata.getComments());
        return entity;
    }
    
    public static ResultsData map(Results results) {
        ResultsData entity = new ResultsData();
        entity.setId(results.getId());
        entity.setCategory(results.getCategory());
        entity.setLowerRange(results.getLowerRange());
        entity.setUpperRange(results.getUpperRange());
        entity.setResults(results.getResults());
        entity.setUnits(results.getUnits());
        entity.setComments(results.getComments());
        return entity;
    }
}
