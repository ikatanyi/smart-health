package io.smarthealth.accounting.billing.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.smarthealth.accounting.billing.domain.enumeration.BillEntryType;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
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
    private String patientNumber;
    private String patientName;
    private String visitNumber;
    private String billNumber;
    private Long itemId;
    private String item;
    private String itemCode;
    private ItemCategory itemCategory;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate billingDate;
    private String transactionId;
    private String receipt;
    private Double quantity = 1.0;
    private Double price;
    private Double discount = 0.0;
    private Double taxes = 0.0;
    private Double amount = 0.0;
    private Double balance = 0.0;
    private String servicePoint;
    private Long servicePointId;
    @JsonProperty(access = Access.WRITE_ONLY)
    private Double deposit = 0.0;
    @JsonProperty(access = Access.WRITE_ONLY)
    private Double payment = 0.0;
    //bill item to a medic 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Long medicId;
    @JsonProperty(access = Access.WRITE_ONLY)
    private String medicName;
    private BillStatus status;
    private Boolean paid;
    private Long requestReference;
    private String paymentReference;
    private Boolean walkinFlag;
    private String createdBy;
    private PaymentMethod paymentMethod;
    private Long schemeId;
    private String invoiceNumber;
    private boolean finalized = false;
    private BillEntryType entryType = BillEntryType.Debit;
    private String schemeName;
    private String paymentStatus;

}
