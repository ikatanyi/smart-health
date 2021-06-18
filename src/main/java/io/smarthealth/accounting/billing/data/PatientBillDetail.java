package io.smarthealth.accounting.billing.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.visit.data.enums.VisitEnum.VisitType;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.infrastructure.lang.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PatientBillDetail {
    private String billNo; // this can be visit_number
    private String patientNumber;
    private String patientName;
    private String visitNumber;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime visitDate;
    private VisitType visitType;
    private BigDecimal totalBillAmount;
    private BigDecimal totalAmountPaid;
    private BigDecimal balance;
    private PaymentMethod paymentMethod;
    private Long payerId;
    private String payerName;
    private Long schemeId;
    private String schemeName;
    private Double copayValue;
    private String copayType;

    public PatientBillDetail() {

    }

    public PatientBillDetail(String billNo, String patientNumber, String patientName, String visitNumber,
                             LocalDateTime visitDate, VisitType visitType, BigDecimal totalBillAmount,
                             BigDecimal totalAmountPaid, BigDecimal balance, PaymentMethod paymentMethod,
                             Long payerId, String payerName, Long schemeId, String schemeName, Double copayValue,
                             String copayType) {
        this.billNo = billNo;
        this.patientNumber = patientNumber;
        this.patientName = patientName;
        this.visitNumber = visitNumber;
        this.visitDate = visitDate;
        this.visitType = visitType;
        this.totalBillAmount = totalBillAmount;
        this.totalAmountPaid = totalAmountPaid;
        this.balance = balance;
        this.paymentMethod = paymentMethod;
        this.payerId = payerId;
        this.payerName = payerName;
        this.schemeId = schemeId;
        this.schemeName = schemeName;
        this.copayValue = copayValue;
        this.copayType = copayType;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getVisitNumber() {
        return visitNumber;
    }

    public void setVisitNumber(String visitNumber) {
        this.visitNumber = visitNumber;
    }

    public LocalDateTime getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDateTime visitDate) {
        this.visitDate = visitDate;
    }

    public VisitType getVisitType() {
        return visitType;
    }

    public void setVisitType(VisitType visitType) {
        this.visitType = visitType;
    }

    public BigDecimal getTotalBillAmount() {
        return totalBillAmount;
    }

    public void setTotalBillAmount(BigDecimal totalBillAmount) {
        this.totalBillAmount = totalBillAmount;
    }

    public BigDecimal getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(BigDecimal totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public Long getPayerId() {
        return payerId;
    }

    public void setPayerId(Long payerId) {
        this.payerId = payerId;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public Double getCopayValue() {
        return copayValue;
    }

    public void setCopayValue(Double copayValue) {
        this.copayValue = copayValue;
    }

    public String getCopayType() {
        return copayType;
    }

    public void setCopayType(String copayType) {
        this.copayType = copayType;
    }
}
