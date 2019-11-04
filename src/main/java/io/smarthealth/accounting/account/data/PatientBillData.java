package io.smarthealth.accounting.account.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.accounting.account.domain.PatientBill;
import io.smarthealth.accounting.account.domain.PatientBillLine;
import io.smarthealth.accounting.account.domain.enumeration.BillStatus;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientBillData {
    private Long id;
    private String patientNumber;
    private String visitNumber;
    private String billNumber;
    private String referenceNumber; //Receipt n. or Invoice No
    private String paymentMode;
    private Double balance;
    private Double Amount;
    private String userNumber;
    
    @Enumerated(EnumType.STRING)
    private BillStatus status;
    
    List<PatientBillLineData>billLines;
    
    public static PatientBillData map(PatientBill bill){
        PatientBillData data=new PatientBillData();      
        data.setId(bill.getId()); 
        data.setAmount(bill.getAmount());
        data.setBalance(bill.getBalance());
        data.setPaymentMode(bill.getPaymentMode());
        data.setStatus(BillStatus.Draft);
        if(bill.getVisit()!=null)
           data.setVisitNumber(bill.getVisit().getVisitNumber());    
        for(PatientBillLine line:bill.getBillLines())
            data.getBillLines().add(PatientBillLineData.map(line));
        return data;    
    }
    
    public static PatientBill map(PatientBillData billdata){
        PatientBill data=new PatientBill();      
        data.setBalance(billdata.getBalance());
        data.setAmount(billdata.getAmount());
        data.setBalance(billdata.getBalance());
        data.setBillingDate(LocalDate.now());
        data.setPaymentMode(billdata.getPaymentMode());
        data.setId(billdata.getId());  
        
        return data;    
    }
    
    
}
