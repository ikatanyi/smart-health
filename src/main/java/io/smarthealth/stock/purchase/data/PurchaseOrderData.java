package io.smarthealth.stock.purchase.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseOrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Kelsas
 */
@Data
public class PurchaseOrderData {
   private Long id;
    private String orderNumber; //PUR-ORD-2019-00001
    private Long supplierId;
    private String supplierName;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate transactionDate;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate requiredDate;
    private Long addressId;
    private String address;
    private Long contactId;
    private String contact;
    private BigDecimal purchaseAmount;
    private Boolean received;
    private Boolean billed;
    private Long storeId;
    private String store;
    private Long priceListId;
    private String priceList;
    private PurchaseOrderStatus status;
    private String createdBy;
    private String remarks;
    private List<PurchaseOrderItemData> purchaseOrderItems;
    @JsonIgnore
    private boolean showItems;

    
}
