/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;

import io.smarthealth.clinical.radiology.data.RadiologyResultData;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
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
public class RadiologyResult extends Identifiable {

    @OneToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_radiology_result_patient_scan_test_id"))
    private PatientScanTest patientScanTest;

    private String notes;
    private String comments;
    private String imagePath;
    @Enumerated(EnumType.STRING)
    private ScanTestState status;
    private LocalDate resultsDate;
    private Boolean voided = Boolean.FALSE;

    public RadiologyResultData toData() {
        RadiologyResultData data = new RadiologyResultData();
        data.setId(this.getId());
        data.setTemplateNotes(this.notes);
        data.setResultsDate(this.resultsDate);
        data.setStatus(this.status);
        data.setComments(this.comments);
        data.setVoided(this.voided);

        if (this.getPatientScanTest().getRadiologyTest() != null) {
            data.setTestId(this.patientScanTest.getRadiologyTest().getId());
            data.setTestCode(this.patientScanTest.getRadiologyTest().getCode());
            data.setTestName(this.patientScanTest.getRadiologyTest().getScanName());
            data.setScanNumber(this.getPatientScanTest().getPatientScanRegister().getAccessNo());
        }

        if (!this.patientScanTest.getPatientScanRegister().getIsWalkin()) {
            data.setPatientName(this.patientScanTest.getPatientScanRegister().getVisit().getPatient().getFullName());
            data.setPatientNo(this.patientScanTest.getPatientScanRegister().getVisit().getPatient().getPatientNumber());
            data.setGender(this.patientScanTest.getPatientScanRegister().getVisit().getPatient().getGender());
            data.setVisitNumber(this.patientScanTest.getPatientScanRegister().getVisit().getVisitNumber());
            data.setVisitDate(this.patientScanTest.getPatientScanRegister().getVisit().getStartDatetime().toLocalDate());
        } else {
            data.setPatientNo(this.patientScanTest.getPatientScanRegister().getPatientNo());
            data.setScanNumber(this.getPatientScanTest().getPatientScanRegister().getAccessNo());
            data.setGender(this.getPatientScanTest().getPatientScanRegister().getGender());
            data.setPatientName("Walkin - " + this.patientScanTest.getPatientScanRegister().getPatientNo());
            data.setVisitNumber(this.patientScanTest.getPatientScanRegister().getPatientNo());
            data.setVisitDate(this.patientScanTest.getPatientScanRegister().getReceivedDate());
        }

        return data;
    }

}
