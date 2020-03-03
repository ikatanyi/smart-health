package io.smarthealth.accounting.billing.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillItemData {

    private Long id;
    private String billNumber;
    private Long itemId;
    private String item;
    private String itemCode;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate billingDate;
    private String transactionId;

    private Double quantity;
    private Double price;

    private Double discount;
    private Double taxes;
    private Double amount;
    private Double balance;

    private String createdBy;
    private String servicePoint;
    private Long servicePointId;

    //bill item to a medic 
    private Long medicId;
    private String medicName;
    
    private BillStatus status;
    private Boolean paid;
    

}
