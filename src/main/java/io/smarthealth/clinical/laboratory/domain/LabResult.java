package io.smarthealth.clinical.laboratory.domain;

import io.smarthealth.clinical.laboratory.data.LabResultData;
import io.smarthealth.infrastructure.domain.Auditable;
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
@Data
@Entity 
@Table(name = "lab_register_results") 
public class LabResult extends Auditable {

//    @ManyToOne
//    @JoinColumn(foreignKey = @ForeignKey(name = "fk_lab_results_visit_id"))
//    private Visit visit;
    private String patientNo;

    private String labNumber;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_lab_results_test_id"))
    private LabRegisterTest labRegisterTest;

    private LocalDateTime resultsDate;
    private String analyte;
    private String resultValue;
    private String units;
    private Double lowerLimit;
    private Double upperLimit;
    private String referenceValue;
    private String status;
    private String comments;
    private Boolean voided = Boolean.FALSE; 
    private String enteredBy;
    private String validatedBy;
    private Boolean resultRead;
    private Boolean validated;
    private Boolean rejected;

    public LabResultData toData() {
        LabResultData data = new LabResultData();
        data.setAnalyte(this.analyte);
        data.setId(this.getId());
        data.setLabNumber(this.labNumber);
        data.setLabRegisterTestId(this.labRegisterTest.getId());
        data.setLowerLimit(this.lowerLimit);
        data.setUpperLimit(this.upperLimit);
        data.setReferenceValue(this.referenceValue);
        data.setResultValue(this.resultValue);
        data.setResultsDate(this.resultsDate);
        data.setStatus(this.status);
        data.setComments(this.comments);
        data.setVoided(this.voided); 
        data.setEnteredBy(this.enteredBy);
        data.setValidatedBy(this.validatedBy);
        data.setResultRead(this.resultRead);

        if (this.labRegisterTest.getLabTest() != null) {
            data.setTestId(this.labRegisterTest.getLabTest().getId());
            data.setTestCode(this.labRegisterTest.getLabTest().getCode());
            data.setTestName(this.labRegisterTest.getLabTest().getTestName());
        }

        if (!this.labRegisterTest.getLabRegister().getIsWalkin()) {
            data.setPatientName(this.labRegisterTest.getLabRegister().getVisit().getPatient().getFullName());
            data.setPatientNo(this.labRegisterTest.getLabRegister().getVisit().getPatient().getPatientNumber());
            data.setVisitNumber(this.labRegisterTest.getLabRegister().getVisit().getVisitNumber());
            data.setVisitDate(this.labRegisterTest.getLabRegister().getVisit().getStartDatetime().toLocalDate());
        } else {
            data.setPatientNo(this.patientNo);
            data.setPatientName("Walkin - " + this.patientNo);
            data.setVisitNumber(this.patientNo);
            data.setVisitDate(this.labRegisterTest.getLabRegister().getRequestDatetime().toLocalDate());
        }
        data.setUnits(this.units);
        data.setValidated(this.validated);
        data.setRejected(this.rejected);

        return data;
    }
 
}
