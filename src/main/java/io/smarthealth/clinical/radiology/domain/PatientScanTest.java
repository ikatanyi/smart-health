/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;

import io.smarthealth.clinical.radiology.data.PatientScanTestData;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.documents.domain.Document;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Employee;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Patient Lab Request
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "patient_scan_test")
public class PatientScanTest extends Auditable {

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_scan_test_radiology_test_id"))
    private RadiologyTest radiologyTest;
    private Double testPrice;
    private Double quantity;
    @Enumerated(EnumType.STRING)
    private ScanTestState status;
    private Boolean done; //results entered
    private Boolean paid = Boolean.FALSE;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_scan_test_employee_id"))
    private Employee doneBy;
    private LocalDateTime entryDateTime;

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_scan_test_request_id"))
    private DoctorRequest request;
    
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_scan_test_radiology_patient_scan_register_id"))
    private PatientScanRegister patientScanRegister;
    private String comments;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_scan_test_radiology_employee_id"))
    private Employee medic;

    @OneToOne(mappedBy = "patientScanTest", cascade = CascadeType.ALL)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_scan_test_radiology_result_id"))
    private RadiologyResult radiologyResult;
    
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_scan_test_document_id"))
    private Document document;

    public PatientScanTestData toData() {
        PatientScanTestData entity = new PatientScanTestData();
        entity.setId(this.getId());
        entity.setComments(this.getComments());
        entity.setTestPrice(this.getTestPrice());
        entity.setQuantity(this.getQuantity());
        entity.setDone(this.getDone());
        entity.setPaid(this.getPaid());
        entity.setReportedBy(this.getCreatedBy());        
        entity.setEntryDateTime(this.getEntryDateTime());
        entity.setStatus(this.getStatus());

        if (patientScanRegister != null) {            
            entity.setReceivedDate(this.getPatientScanRegister().getReceivedDate());
            entity.setAccessNo(this.getPatientScanRegister().getAccessNo());
            entity.setPatientName(this.getPatientScanRegister().getPatientName());
            entity.setPatientNumber(this.getPatientScanRegister().getPatientNo());
            entity.setIsWalkin(this.getPatientScanRegister().getIsWalkin());           
            entity.setOrderedDate(this.getPatientScanRegister().getReceivedDate());
            entity.setReferenceNo(this.getPatientScanRegister().getTransactionId());
            entity.setPaymentMode(this.getPatientScanRegister().getPaymentMode());
            if(this.getPatientScanRegister().getVisit()!=null) {
                if(this.getPatientScanRegister().getVisit().getHealthProvider()!=null)
                    entity.setRequestedByStaffNumber(this.getPatientScanRegister().getVisit().getHealthProvider().getStaffNumber()); 
               entity.setVisitNumber(this.getPatientScanRegister().getVisit().getVisitNumber());
            }
        }

        if (this.getRadiologyTest() != null) {
            entity.setScanName(this.getRadiologyTest().getScanName());
            entity.setTestCode(this.getRadiologyTest().getItem().getItemCode());
        }
        if (this.getMedic() != null) {
            entity.setDoneBy(this.getMedic().getFullName());
        }
        if (this.getRadiologyResult() != null) {
            entity.setResultData(this.getRadiologyResult().toData());
            entity.setReportResultData(Arrays.asList(this.getRadiologyResult().toData()));
        }
         if(this.getDocument()!=null)
            entity.setDocumentData(this.getDocument().toData());

        entity.setPatientNumber(this.getPatientScanRegister().getPatientNo());
        entity.setPatientName(this.getPatientScanRegister().getPatientName());
        entity.setSupervisorConfirmation(this.getRadiologyTest().getSupervisorConfirmation());
        if (this.getRadiologyTest().getServiceTemplate() != null) {
            entity.setTemplateName(this.getRadiologyTest().getServiceTemplate().getTemplateName());
            entity.setTemplateId(this.getRadiologyTest().getServiceTemplate().getId());
            if (this.getRadiologyTest().getServiceTemplate().getNotes() != null) {
                entity.setTemplate(new String(this.getRadiologyTest().getServiceTemplate().getNotes(), StandardCharsets.UTF_8));
            }
        }
        if (this.getRequest() != null) {
            entity.setRequestId(this.getRequest().getId());
            if (this.getRequest().getRequestedBy() != null) {
                entity.setRequestedBy(this.getRequest().getRequestedBy().getName());  
            }
        }
        return entity;
    }

}
