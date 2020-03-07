package io.smarthealth.clinical.laboratory.domain;

import io.smarthealth.clinical.laboratory.data.LabRequestData;
import io.smarthealth.clinical.laboratory.domain.enumeration.TestStatus;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "lab_patient_requests")  //lab_patient_rquests
public class LabRequest extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_lab_request_visit_id"))
    private Visit visit;

    private String orderNumber; //reference doctor's request

    private String patientNo;

    private LocalDateTime requestDatetime;

    private String labNumber;

    private String requestedBy;

    private Boolean isWalkin;

    @Enumerated(EnumType.STRING)
    private TestStatus status;

    private Boolean voided = Boolean.FALSE;

    @OneToMany(mappedBy = "labRequest", cascade = CascadeType.ALL)
    private List<LabRequestTest> tests = new ArrayList<>();

    public void addPatientTest(LabRequestTest test) {
        test.setLabRequest(this);
        tests.add(test);
    }

    public void addPatientTest(List<LabRequestTest> tests) {
        this.tests = tests;
        this.tests.forEach(x -> x.setLabRequest(this));
    }

    public LabRequestData toData(Boolean expand) {
        LabRequestData data = new LabRequestData();
        data.setId(this.getId());
        data.setLabNumber(this.labNumber);
        data.setOrderNumber(this.orderNumber);
        if (this.visit != null && !this.isWalkin) {
            data.setPatientName(this.visit.getPatient().getFullName());
            data.setVisitNumber(this.visit.getVisitNumber());
        } else {
            data.setPatientName(this.patientNo);
            data.setVisitNumber(this.patientNo);
        }
        data.setPatientNo(this.patientNo);
        data.setRequestDatetime(this.requestDatetime);
        data.setRequestedBy(this.requestedBy);
        data.setStatus(this.status);

        if (expand != null && expand) {
            data.setTests(this.tests
                    .stream()
                    .map(x -> x.toData())
                    .collect(Collectors.toList())
            );
        }

        return data;
    }
}
