package io.smarthealth.billing.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.billing.domain.enumeration.BillStatus;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillData {
    private Long id;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate billingDate;
    private String patientNumber;
    private String patientName;
    private String visitNumber;
    private String billNumber;
    private String referenceNumber; //Receipt n. or Invoice No
    private String paymentMode;
    private Double balance;
    private Double Amount;
    private Double discount;
    private String journalNumber;
    
    @Enumerated(EnumType.STRING)
    private BillStatus status;
    
    private List<BillItemData>billItems; 
    
//    public static PatientBillData map(PatientBill bill){
//        PatientBillData data=new PatientBillData();      
//        data.setId(bill.getId()); 
//        data.setBillNumber(bill.getBillNumber()); 
//        data.setBillingDate(bill.getBillingDate());
//        data.setJournalNumber(bill.getJournalNumber());
//        data.setReferenceNumber(bill.getReferenceNumber());
//        data.setAmount(bill.getAmount());
//        data.setBalance(bill.getBalance());
//        data.setPaymentMode(bill.getPaymentMode());
//        data.setStatus(bill.getStatus());
//        
//        if(bill.getVisit()!=null){ 
//            data.setVisitNumber(bill.getVisit().getVisitNumber());
//            data.setPatientNumber(bill.getVisit().getPatient().getPatientNumber());
//            data.setPatientName(bill.getPatient().getFullName());
//        }
//        
//        List<PatientBillItemData> billItems=bill.getBillLines().stream()
//                .map(b -> PatientBillItemData.map(b))
//                .collect(Collectors.toList());
//        
//        data.setBillItems(billItems);
//       
//        
//        return data;    
//    }
     
}
