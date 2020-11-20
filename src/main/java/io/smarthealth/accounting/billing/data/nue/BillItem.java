package io.smarthealth.accounting.billing.data.nue;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.billing.domain.enumeration.BillPayMode;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class BillItem {
    private Long id;
    private Long billId;
    private Long itemId;
    private String item;
    private String itemCode;
    private ItemCategory itemCategory;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate billingDate;
    private Double quantity;
    private Double price;
    private Double amount;
    private Double discount;
    private Double tax;
    private Double netAmount;
    private String servicePoint;
    private Long servicePointId;
    private String transactionId;
    private String reference;
    private Boolean paid;
    
    @Enumerated(EnumType.STRING)
    private BillStatus status;
    
     @Enumerated(EnumType.STRING)
    private BillPayMode billPayMode;
}
