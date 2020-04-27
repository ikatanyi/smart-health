/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.clinical.procedure.domain.PatientProcedureRegister;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) 	//  ignore all null fields
public class PatientProcedureRegisterData {

    @ApiModelProperty(hidden = true, required = false)
    private LocalDate orderedDate;

    @ApiModelProperty(hidden = true, required = false)
    private String visitNumber;
    private Long requestId;
    @ApiModelProperty(hidden = true, required = false)
    private String accessionNo;
    //@ApiModelProperty(hidden = true, required = false)
    private String patientNumber;

//    @ApiModelProperty(hidden = true, required = false)
    private String patientName;
    @NotNull(message = "Walk-in flag is required")
    private Boolean isWalkin = Boolean.FALSE;
    @ApiModelProperty(required = false)
    private String requestedBy;
    @ApiModelProperty(hidden = true, required = false)
    private String physicianName;
    @ApiModelProperty(required = false, hidden = true)
    private LocalDate receivedDate;
    @ApiModelProperty(required = false, hidden = true)
    private LocalDate createdOn;
    @ApiModelProperty(required = false, hidden = true)
    private String createdBy;

    @ApiModelProperty(required = false, hidden = true)
    private DoctorRequestData requestData;

    private Long servicePointId;

    @ApiModelProperty(required = false, hidden = true)
    private String billNumber;
    @ApiModelProperty(required = false, hidden = true)
    private String transactionId; //Receipt n. or Invoice No
    private String paymentMode;
    @ApiModelProperty(required = false, hidden = true)
    private Double balance;
    @ApiModelProperty(required = false, hidden = true)
    private Double Amount;
    @ApiModelProperty(required = false, hidden = true)
    private Double taxes;
    private Double discount;

    private LocalDate billingDate;

    @ApiModelProperty(hidden = true, required = false)
    private List<PatientProcedureTestData> patientProcecedureTestData = new ArrayList();

    private List<ProcedureItemData> itemData = new ArrayList();

    @ApiModelProperty(required = false, hidden = true)
    private BillData billData;

    public static PatientProcedureRegister map(PatientProcedureRegisterData patientregister) {
        PatientProcedureRegister e = new PatientProcedureRegister();
        e.setAccessNo(patientregister.getAccessionNo());
        //e.setAmount(patientregister.getAmount());
        e.setDiscount(patientregister.getDiscount());
        e.setBillingDate(patientregister.getBillingDate());
        return e;
    }
}
