/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 *
 * @author kent
 */
class ResultsData {
    private Long id;
    private String testCode;
    private String testType;
    private String testName;
    private String normalRange;
    private String units;
    private String category;
    private String results;
    private String comments;
}
