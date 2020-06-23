package io.smarthealth.clinical.laboratory.domain;

import io.smarthealth.clinical.laboratory.data.LabRegisterData;
import io.smarthealth.clinical.laboratory.domain.enumeration.LabTestStatus;
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
@Table(name = "lab_register")  //lab_patient_rquests 
public class LabRegister extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_lab_request_visit_id"))
    private Visit visit;

    private String orderNumber; //reference doctor's request

    private String patientNo;

    private LocalDateTime requestDatetime;

    private String labNumber;

    private String requestedBy;

    private Boolean isWalkin;

    private String paymentMode;

    private String transactionId;

    @Enumerated(EnumType.STRING)
    private LabTestStatus status;

    private Boolean voided = Boolean.FALSE;

    @OneToMany(mappedBy = "labRegister", cascade = CascadeType.ALL)
    private List<LabRegisterTest> tests = new ArrayList<>();

    public void addPatientTest(LabRegisterTest test) {
        test.setLabRegister(this);
        tests.add(test);
    }

    public void addPatientTest(List<LabRegisterTest> tests) {
        this.tests = tests;
        this.tests.forEach(x -> x.setLabRegister(this));
    }

    public LabRegisterData toData(Boolean expand) {
        LabRegisterData data = new LabRegisterData();
        data.setId(this.getId());
        data.setLabNumber(this.labNumber);
        data.setOrderNumber(this.orderNumber);
        data.setIsWalkin(this.isWalkin);
        if (this.visit != null && !this.isWalkin) {
            data.setPatientName(this.visit.getPatient().getFullName());
            data.setVisitNumber(this.visit.getVisitNumber());
            if (this.visit.getHealthProvider() != null) {
                data.setRequestedByStaffNumber(this.visit.getHealthProvider().getStaffNumber());
            }
        } else {

            data.setPatientName(this.requestedBy);
            data.setVisitNumber(this.patientNo);
        }
        data.setPatientNo(this.patientNo);
        data.setRequestDatetime(this.requestDatetime);
        data.setRequestedBy(this.requestedBy);
        data.setStatus(this.status);
        data.setTransactionId(this.transactionId);
        data.setPaymentMode(this.paymentMode);
        if (expand != null && expand) {
            data.setTests(this.tests
                    .stream()
                    .map(x -> x.toData(expand))
                    .collect(Collectors.toList())
            );

        }
        return data;
    }

    public boolean isCompleted() {
        long pending = this.getTests()
                .stream()
                .filter(x -> x.getStatus() == LabTestStatus.PendingResult || x.getStatus() == LabTestStatus.AwaitingSpecimen)
                .count();
        System.err.println("Pending tests to report results... " + pending);
        return pending == 1;
    }
}
