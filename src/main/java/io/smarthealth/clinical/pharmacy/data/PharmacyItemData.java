/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.pharmacy.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class PharmacyItemData {

    private Long id;
    private String billNumber;
    private Long itemId;
    private String item;
    private String itemCode;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate billingDate;
    private String transactionId;
    private Double qtyIssued;
    private Double quantity;
    private Double price;
    private Double discount;
    private Double taxes;
    private Double amount;
    private String unit;
    private String createdBy;
    
    private String servicePoint;
    private Long servicePointId;

    private BillStatus status;
    private Boolean paid;
    private String instructions;
    private String doctorName; 
    private Boolean collected;
    private String dispensedBy;
    private String collectedBy;
}
