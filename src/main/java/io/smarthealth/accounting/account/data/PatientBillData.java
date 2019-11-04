package io.smarthealth.accounting.account.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.accounting.account.domain.PatientBill;
import io.smarthealth.accounting.account.domain.enumeration.BillStatus;
import java.time.LocalDate;
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
    private String paymentMode;
    private Long itemId; 
    private String item;
    private String itemCode;
    private Double quantity;
    private Double price;
    private Double amount;
    private Long userId;
    private String username;
    private LocalDate billingDate;
    private Long departrmentId;
    private String departmentName;
    private Double balance;
    
    @Enumerated(EnumType.STRING)
    private BillStatus status;
    
    public static PatientBillData map(PatientBill bill){
        PatientBillData data=new PatientBillData();        
        data.setAmount(bill.getAmount());
        data.setBalance(bill.getBalance());
        data.setBillingDate(bill.getBillingDate());
        data.setPaymentMode(bill.getPaymentMode());
        data.setPrice(bill.getPrice());
        data.setQuantity(bill.getQuantity());
        data.setId(bill.getId());  
        
        if(bill.getItem()!=null){
            data.setItemId(bill.getItem().getId());
            data.setItemCode(bill.getItem().getItemCode());
            data.setItem(bill.getItem().getItemName());
        }
        if(bill.getDepartrment()!=null){
            data.setDepartmentName(bill.getDepartrment().getName());
            data.setDepartrmentId(bill.getDepartrment().getId());
        }
          
        if(bill.getPatient()!=null)
           data.setPatientNumber(bill.getPatient().getPatientNumber());
        if(bill.getUser()!=null){
            data.setStatus(BillStatus.Draft);
            data.setUserId(bill.getUser().getId());
            data.setUsername(bill.getUser().getUsername());
        }
       
        if(bill.getVisit()!=null)
           data.setVisitNumber(bill.getVisit().getVisitNumber());
        return data;    
    }
    
    public static PatientBill map(PatientBillData billdata){
        PatientBill data=new PatientBill();        
        data.setAmount(billdata.getAmount());
        data.setBalance(billdata.getBalance());
        data.setBillingDate(billdata.getBillingDate());
        data.setPaymentMode(billdata.getPaymentMode());
        data.setPrice(billdata.getPrice());
        data.setQuantity(billdata.getQuantity());
        data.setId(billdata.getId());  
        
        return data;    
    }
    
    
}
