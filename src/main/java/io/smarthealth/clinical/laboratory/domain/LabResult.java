package io.smarthealth.clinical.laboratory.domain;

import io.smarthealth.clinical.laboratory.data.LabResultData;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "lab_patient_result")
public class LabResult extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_lab_results_visit_id"))
    private Visit visit;
    private String patientNo;
    private String labNumber;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_lab_results_test_id"))
    private LabRequestTest labRequestTest;
    private LocalDateTime resultsDate;
    private String analyte;
    private String resultValue;
    private String units;
    private Double lowerLimit;
    private Double upperLimit;
    private String referenceValue;
    private Boolean voided = Boolean.FALSE;
    
    public LabResultData toData() {
        LabResultData data = new LabResultData();
        data.setAnalyte(this.analyte);
        data.setId(this.getId());
        data.setLabNumber(this.labNumber);
        data.setLabRequestTestId(this.labRequestTest.getId());
        data.setLowerLimit(this.lowerLimit);
        data.setUpperLimit(this.upperLimit);
        data.setReferenceValue(this.referenceValue);
        data.setResultValue(this.resultValue);
        data.setResultsDate(this.resultsDate);
        data.setVoided(this.voided);

        if (this.labRequestTest.getLabTest() != null) {
            data.setTestId(this.labRequestTest.getLabTest().getId());
            data.setTestCode(this.labRequestTest.getLabTest().getCode());
            data.setTestName(this.labRequestTest.getLabTest().getTestName());
        }

        if (this.visit != null && !this.labRequestTest.getLabRequest().getIsWalkin()) {
            data.setPatientName(this.visit.getPatient().getFullName());
            data.setVisitNumber(this.visit.getVisitNumber());
            data.setVisitDate(this.visit.getStartDatetime().toLocalDate());
        } else {
            data.setPatientName(this.patientNo);
            data.setVisitNumber(this.patientNo);
            data.setVisitDate(this.labRequestTest.getLabRequest().getRequestDatetime().toLocalDate());
        }
        data.setUnits(this.units);

        return data;
    }
}
