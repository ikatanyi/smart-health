package io.smarthealth.accounting.billing.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;

//@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LimitExceedingResponse  {


    @Enumerated(EnumType.STRING)
    private LimitResponseStatus responseStatus;
    private String message;
    private String visitNumber;
    private String patientNumber;
    private Double excessAmount;
    private Double totalBillAmount;
    private Double runningLimitAmount;

    private String code;
    @JsonIgnore
    private HttpStatus httpStatus;

    List<BillItemData> billItemData = new ArrayList<>();

    public  LimitExceedingResponse(){

    }

    public LimitExceedingResponse(LimitResponseStatus limitResponseStatus, String message, String visitNumber,
                                  String patientNumber, Double excessAmount, Double totalBillAmount,
                                  Double runningLimitAmount, List<BillItemData> billItemData) {
        this();
        this.responseStatus = limitResponseStatus;
        this.message = message;
        this.visitNumber = visitNumber;
        this.patientNumber = patientNumber;
        this.excessAmount = excessAmount;
        this.totalBillAmount = totalBillAmount;
        this.runningLimitAmount = runningLimitAmount;
        this.billItemData = billItemData;
    }

    public LimitExceedingResponse(LimitResponseStatus limitResponseStatus, String visitNumber,
                                  String patientNumber) {
        this();
        this.responseStatus = limitResponseStatus;
        this.visitNumber = visitNumber;
        this.patientNumber = patientNumber;
    }

    public LimitResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(LimitResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

//    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVisitNumber() {
        return visitNumber;
    }

    public void setVisitNumber(String visitNumber) {
        this.visitNumber = visitNumber;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public Double getExcessAmount() {
        return excessAmount;
    }

    public void setExcessAmount(Double excessAmount) {
        this.excessAmount = excessAmount;
    }

    public Double getTotalBillAmount() {
        return totalBillAmount;
    }

    public void setTotalBillAmount(Double totalBillAmount) {
        this.totalBillAmount = totalBillAmount;
    }

    public Double getRunningLimitAmount() {
        return runningLimitAmount;
    }

    public void setRunningLimitAmount(Double runningLimitAmount) {
        this.runningLimitAmount = runningLimitAmount;
    }

    public List<BillItemData> getBillItemData() {
        return billItemData;
    }

    public void setBillItemData(List<BillItemData> billItemData) {
        this.billItemData = billItemData;
    }

    @Override
    public String toString() {
        return "LimitExceedingResponse{" +
                "responseStatus=" + responseStatus +
                ", message='" + message + '\'' +
                ", visitNumber='" + visitNumber + '\'' +
                ", patientNumber='" + patientNumber + '\'' +
                ", excessAmount=" + excessAmount +
                ", totalBillAmount=" + totalBillAmount +
                ", runningLimitAmount=" + runningLimitAmount +
                ", code='" + code + '\'' +
                '}';
    }
}


