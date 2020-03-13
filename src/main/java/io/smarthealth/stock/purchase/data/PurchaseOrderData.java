package io.smarthealth.stock.purchase.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.stock.purchase.domain.PurchaseOrder;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseOrderStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

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
    private List<PurchaseOrderItemData> purchaseOrderItems;
    @JsonIgnore
    private boolean showItems;

    public static PurchaseOrderData map(PurchaseOrder order) {
        PurchaseOrderData data = new PurchaseOrderData();
        data.setId(order.getId());
        data.setOrderNumber(order.getOrderNumber());
        if (order.getSupplier() != null) {
            data.setSupplierId(order.getSupplier().getId());
            data.setSupplierName(order.getSupplier().getSupplierName());
        }
        data.setTransactionDate(order.getTransactionDate());
        data.setRequiredDate(order.getRequiredDate());
        if (order.getAddress() != null) {
            data.setAddressId(order.getAddress().getId());
        }
        if (order.getContact() != null) {
            data.setContact(order.getContact().getFullName());
            data.setContactId(order.getContact().getId());
        }
        if (order.getStore() != null) {
            data.setStoreId(order.getId());
            data.setStore(order.getStore().getStoreName());
        }
        if (order.getPriceList() != null) {
            data.setStoreId(order.getPriceList().getId());
            data.setPriceList(order.getPriceList().getName());
        }
        data.setStatus(order.getStatus());
        data.setPurchaseAmount(order.getPurchaseAmount());
        data.setBilled(order.getBilled());
        data.setReceived(order.getReceived());
        data.setCreatedBy(order.getCreatedBy());

        List<PurchaseOrderItemData> list = order.getPurchaseOrderLines()
                .stream()
                .map(item -> PurchaseOrderItemData.map(item))
                .collect(Collectors.toList());

        data.setPurchaseOrderItems(list);

        return data;
    }
}
