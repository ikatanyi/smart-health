/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.data;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.clinical.radiology.domain.PatientScanRegister;
import io.smarthealth.infrastructure.lang.DateConverter;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) 	//  ignore all null fields
public class PatientScanRegisterData {

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

    private String servicePoint;

    @ApiModelProperty(hidden = true, required = false)
    private List<PatientScanTestData> patientScanTestData = new ArrayList();

    private List<ScanItemData> itemData = new ArrayList();

    @ApiModelProperty(required = false, hidden = true)
    private BillData billData;

    public static PatientScanRegister map(PatientScanRegisterData patientregister) {
        PatientScanRegister e = new PatientScanRegister();
        e.setAccessNo(patientregister.getAccessionNo());
        return e;
    }

    public static PatientScanRegisterData map(PatientScanRegister patientregister) {
        PatientScanRegisterData data = new PatientScanRegisterData();
        if (patientregister.getVisit() != null) {
            data.setVisitNumber(patientregister.getVisit().getVisitNumber());
            data.setPatientName(patientregister.getVisit().getPatient().getFullName());
            data.setPatientNumber(patientregister.getVisit().getPatient().getPatientNumber());
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

        if (patientregister.getPatientScanTest()!= null) {
            data.setPatientScanTestData(
                    patientregister.getPatientScanTest()
                    .stream()
                    .map((pscantest)->PatientScanTestData.map(pscantest))
                    .collect(Collectors.toList())
            );
        }
        //data.setOrderedDate(DateConverter.toIsoString(LocalDateTime.ofInstant(patientregister.getCreatedOn(), ZoneOffset.UTC)));

        //System.out.println("Date converted " + DateConverter.toLocalDate(LocalDateTime.ofInstant(patientregister.getCreatedOn(), ZoneOffset.UTC)));
        data.setOrderedDate(DateConverter.toLocalDate(LocalDateTime.ofInstant(patientregister.getCreatedOn(), ZoneOffset.UTC)));

        return data;
    }

}
