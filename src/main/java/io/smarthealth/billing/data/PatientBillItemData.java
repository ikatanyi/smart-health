package io.smarthealth.billing.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.billing.domain.PatientBillItem;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientBillItemData {

    private String billNumber;
    private Long itemId;
    private String item;
    private String itemCode;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate billingDate;
    private Double quantity;
    private Double price;
    private Double amount;
    private Double discount;
    private String transactionNo;
    private String createdBy;
   private Long servicePointId;
    private String servicePoint;

//    public static PatientBillItemData map(PatientBillItem bill) {
//        PatientBillItemData data = new PatientBillItemData();
//        data.setBillNumber(bill.getPatientBill().getBillNumber());
//        data.setBillingDate(bill.getBillingDate());
//        data.setPrice(bill.getPrice());
//        data.setQuantity(bill.getQuantity());
//        data.setAmount(bill.getAmount());
//        data.setTransactionNo(bill.getTransactionNo());
//        data.setCreatedBy(bill.getCreatedBy());
//
//        if (bill.getItem() != null) {
//            data.setItemId(bill.getItem().getId());
//            data.setItemCode(bill.getItem().getItemCode());
//            data.setItem(bill.getItem().getItemName()); 
//        }
//            data.setServicePoint(bill.getServicePoint());
//            data.setServicePointId(bill.getServicePointId());
//        
//
//        return data;
//    }

}
