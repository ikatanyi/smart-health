/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;

import io.smarthealth.clinical.radiology.data.RadiologyResultData;
import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "radiology_results")
public class RadiologyResult extends Auditable{

    @OneToOne(mappedBy = "radiologyResult")
    private PatientScanTest patientScanTest;
    
    private String scanNumber;

    private String patientNo; 
    private String patientNumber; 
    private String notes;
    private Long testId;
    private String testName;
    private String comments;
    private String imagePath;
    private String status;
    private LocalDateTime resultsDate;
    private Boolean voided = Boolean.FALSE;    

    public RadiologyResultData toData() {
        RadiologyResultData data = new RadiologyResultData();
        data.setId(this.getId());
        data.setScanNumber(this.scanNumber);
        data.setTemplateNotes(this.notes);
        data.setResultsDate(this.resultsDate);
        data.setStatus(this.status);
        data.setComments(this.comments);
        data.setVoided(this.voided);

        if (this.patientScanTest.getRadiologyTest() != null) {
            data.setTestId(this.patientScanTest.getRadiologyTest().getId());
            data.setTestCode(this.patientScanTest.getRadiologyTest().getCode());
            data.setTestName(this.patientScanTest.getRadiologyTest().getScanName());
        } 
        
        if (!this.patientScanTest.getPatientScanRegister().getIsWalkin()) {
            data.setPatientName(this.patientScanTest.getPatientScanRegister().getVisit().getPatient().getFullName());
            data.setPatientNo(this.patientScanTest.getPatientScanRegister().getVisit().getPatient().getPatientNumber());
            data.setVisitNumber(this.patientScanTest.getPatientScanRegister().getVisit().getVisitNumber());
            data.setVisitDate(this.patientScanTest.getPatientScanRegister().getVisit().getStartDatetime().toLocalDate());
        } else {
            data.setPatientNo(this.patientNo);
            data.setPatientName("Walkin - "+this.patientNo);
            data.setVisitNumber(this.patientNo);
            data.setVisitDate(this.patientScanTest.getPatientScanRegister().getRequestDatetime().toLocalDate());
        }

        return data;
    }
    
}
