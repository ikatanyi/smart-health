/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.data;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.clinical.radiology.domain.PatientScanRegister;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    private Long requestId;
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
    @ApiModelProperty(hidden = true, required = false)
    private LocalDate createdOn;
    private Boolean isWalkin;

    private String servicePoint;

    @ApiModelProperty(hidden = true, required = false)
    private List<PatientScanTestData> patientScanTestData = new ArrayList();

    private List<ScanItemData> itemData = new ArrayList();

    @ApiModelProperty(required = false, hidden = true)
    private BillData billData;

    public static PatientScanRegister map(PatientScanRegisterData patientregister) {
        PatientScanRegister e = new PatientScanRegister();
        e.setAccessNo(patientregister.getAccessionNo());
        e.setIsWalkin(patientregister.getIsWalkin());
        return e;
    }
}
