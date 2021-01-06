/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.clinical.radiology.domain.PatientScanRegister;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    @ApiModelProperty(hidden = true, required = false)
    private String accessionNo;
    private String patientNumber;

    private String patientName;
    @ApiModelProperty(hidden = true, required = false)
    private String billNumber;
    @ApiModelProperty(required = false)
    private String requestedBy;
    @ApiModelProperty(hidden = true, required = false)
    private String requestedById;
    @ApiModelProperty(hidden = true, required = false)
    private LocalDate createdOn;
    private LocalDate billingDate;
    @ApiModelProperty(hidden = true, required = false)
    private LocalDate receivedDate=LocalDate.now();
    private Boolean isWalkin;
    private Boolean voided;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Double amount;

    private Double Discount;

    private String paymentMode;

    private Double balance;

    private String servicePoint;

    @ApiModelProperty(hidden = true, required = false)
    private String transactionId;

    @ApiModelProperty(hidden = true, required = false)
    private List<PatientScanTestData> patientScanTestData = new ArrayList();

    private List<ScanItemData> itemData = new ArrayList();

    @ApiModelProperty(required = false, hidden = true)
    private BillData billData;

    public PatientScanRegister fromData() {
        PatientScanRegister e = new PatientScanRegister();
        e.setAccessNo(this.getAccessionNo());
        e.setIsWalkin(this.getIsWalkin());
        if(this.getIsWalkin())
            e.setRequestedBy(this.getRequestedBy());
        e.setAmount(this.getAmount());
        e.setDiscount(this.getDiscount());
        e.setBalance(this.getBalance());
        e.setPaymentMode(this.getPaymentMode());
        e.setBillingDate(this.getBillingDate());
        e.setGender(this.getGender());
        e.setVoided(this.getVoided());
        e.setReceivedDate(this.getReceivedDate());
        return e;
    }
}
