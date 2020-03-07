package io.smarthealth.clinical.laboratory.domain;

import io.smarthealth.clinical.laboratory.data.LabRequestTestData;
import io.smarthealth.clinical.laboratory.domain.enumeration.TestStatus;
import io.smarthealth.infrastructure.domain.Identifiable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "lab_request_tests")
public class LabRequestTest extends Identifiable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_lab_request_tests_request_id"))
    private LabRequest labRequest;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_lab_request_tests_test_id"))
    private LabTest labTest;
    
    private Long requestId; //reference requence 
    private String specimen;
    private LocalDateTime collectionDateTime;
    private String collectedBy;
    private Boolean collected; // sample collected
    private Boolean paid; //  test paid
    private String referenceNo; //payment reference
    private Boolean entered; //results entered
    @Enumerated(EnumType.STRING)
    private TestStatus status;
    private Boolean voided = Boolean.FALSE;

    public LabRequestTestData toData() {
        LabRequestTestData data = new LabRequestTestData();
        data.setId(this.getId());
        data.setCollected(this.collected);
        data.setCollectedBy(this.collectedBy);
        data.setCollectionDateTime(this.collectionDateTime);
        data.setEntered(this.entered);
        if (this.labRequest != null) {
            data.setLabRequestId(this.labRequest.getId());
            data.setOrderNumber(this.labRequest.getOrderNumber());
            data.setRequestedBy(this.labRequest.getRequestedBy());
        }

        data.setPaid(this.paid);
        data.setReferenceNo(this.referenceNo);
        data.setRequestId(this.requestId);

        data.setSpecimen(this.specimen);
        data.setStatus(this.status);
        if (this.labTest != null) {
            data.setTestId(this.labTest.getId());
            data.setTestCode(this.labTest.getCode());
            data.setTestName(this.labTest.getTestName());
        }
        return data;

    }
}
