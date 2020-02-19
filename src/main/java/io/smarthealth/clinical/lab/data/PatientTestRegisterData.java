/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.clinical.lab.domain.LabRegister;
import io.smarthealth.infrastructure.lang.DateConverter;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) 	//  ignore all null fields
public class PatientTestRegisterData {

    @ApiModelProperty(hidden = true, required = false)
    private LocalDate orderedDate;

    @ApiModelProperty(hidden = true, required = false)
    private String visitNumber;
    @ApiModelProperty(hidden = true, required = false)
    private String requestId;
    private String accessionNo;
    private String patientNumber;

    @ApiModelProperty(hidden = true, required = false)
    private String patientName;
    @ApiModelProperty(hidden = true, required = false)
    private String billNumber;
    @ApiModelProperty(required = false)
    private String requestedBy;
    @ApiModelProperty(hidden = true, required = false)
    private String physicianName;
    private LocalDate receivedDate;
    private LocalDate dateOfBith;
    private Integer age;

    private String servicePoint;

    @ApiModelProperty(hidden = true, required = false)
    private List<PatientLabTestData> patientLabTestData = new ArrayList();

    private List<TestItemData> itemData = new ArrayList();

    @ApiModelProperty(required = false, hidden = true)
    private BillData billData;

    public static LabRegister map(PatientTestRegisterData patientregister) {
        LabRegister e = new LabRegister();
        e.setAccessNo(patientregister.getAccessionNo());
        return e;
    }

    public static PatientTestRegisterData map(LabRegister patientregister) {
        PatientTestRegisterData data = new PatientTestRegisterData();
        if (patientregister.getVisit() != null) {
            data.setVisitNumber(patientregister.getVisit().getVisitNumber());
            data.setPatientName(patientregister.getVisit().getPatient().getFullName());
            data.setPatientNumber(patientregister.getVisit().getPatient().getPatientNumber());
            data.setDateOfBith(patientregister.getVisit().getPatient().getDateOfBirth());
            data.setAge(patientregister.getVisit().getPatient().getAge());
        }
        if (patientregister.getRequest() != null) {
            data.setRequestId(String.valueOf(patientregister.getRequest().getId()));
            data.setRequestedBy(patientregister.getRequest().getRequestedBy().getStaffNumber());
            data.setPhysicianName(patientregister.getRequest().getCreatedBy());
        }
        data.setAccessionNo(patientregister.getAccessNo());

      
        if (patientregister.getBill() != null) {
            data.setBillNumber(patientregister.getBill().getBillNumber());
            data.setBillData(patientregister.getBill().toData());
        }

        if (patientregister.getPatientLabTest() != null) {
            data.setPatientLabTestData(PatientLabTestData.map(patientregister.getPatientLabTest()));
        }
        
        data.setOrderedDate(DateConverter.toLocalDate(LocalDateTime.ofInstant(patientregister.getCreatedOn(), ZoneOffset.UTC)));

        return data;
    }

}
