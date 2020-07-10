package io.smarthealth.clinical.pharmacy.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class DrugRequest {

    private Long id; 
    private Long storeId;
    private String storeName;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate dispenseDate;
    private String patientNumber;
    private String patientName;
    private String visitNumber;
    private String billNumber;
    private String transactionId; //Receipt n. or Invoice No
    private String paymentMode;
    private Double balance;
    private Double Amount;
    private Double taxes;
    private Double discount; 
     private Boolean isWalkin; 
      
    @Enumerated(EnumType.STRING)
    private BillStatus status;

    private List<DrugItemRequest> drugItems = new ArrayList<>();
}
