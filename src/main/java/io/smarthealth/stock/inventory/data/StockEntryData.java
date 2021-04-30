package io.smarthealth.stock.inventory.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Stock Entry
 *
 * @author Kelsas
 */
@Data
public class StockEntryData {

    private Long id;

    private Long storeId;
    private String store;

    private Long itemId;
    private String itemCode;
    private String item;

    private Double quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private String unit;
    private BigDecimal totalExclusive;
    private BigDecimal totalAmount;
    private String referenceNumber; //ref LPO,supplier, patient no
    private String deliveryNumber; //GRN| transaction reference
    private String transactionNumber; //auto generated ST-2019-00002
    private String costCenter;
    private String issuedTo;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate transactionDate;
    private MovementType moveType;
    private MovementPurpose purpose;
     private LocalDate expiryDate;
    private String batchNo;
    private String createdBy;
    private String category;
    private Double cachedQuantity;
    private Long destinationStoreId;
    private String destinationStore;
    private StockEntry.Status status;
    @JsonFormat(pattern = "MMM dd yyyy")
    private LocalDate dateCreated;
    @JsonFormat(pattern = "MMM dd yyyy")
    private LocalDateTime lastUpdated;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal costPrice;
    private Double formattedQuantity;
    private BigDecimal formattedTotal;
    private Double fixedQuantity;

    public static StockEntryData map(StockEntry stock) {
        StockEntryData data = new StockEntryData();
        data.setId(stock.getId());
        if (stock.getStore() != null) {
            data.setStoreId(stock.getStore().getId());
            data.setStore(stock.getStore().getStoreName());
        }
        if(stock.getDestinationStore()!=null){
            data.setDestinationStore(stock.getDestinationStore().getStoreName());
            data.setDestinationStoreId(stock.getDestinationStore().getId());
        }
        if (stock.getItem() != null) {
            data.setItemId(stock.getItem().getId());
            data.setItemCode(stock.getItem().getItemCode());
            data.setItem(stock.getItem().getItemName());
            data.setCategory(stock.getItem().getDrugCategory());
        }

        data.setUnit(stock.getUnit());
        data.setQuantity(stock.getQuantity());
        data.setFixedQuantity(stock.getQuantity());
        data.setPrice(stock.getPrice());
        data.setAmount(stock.getAmount());
        data.setReferenceNumber(stock.getReferenceNumber());
        data.setDeliveryNumber(stock.getDeliveryNumber());
        data.setTransactionNumber(stock.getTransactionNumber());
        data.setCostCenter(stock.getCostCenter());
        data.setTransactionDate(stock.getTransactionDate());
        data.setMoveType(stock.getMoveType());
        data.setPurpose(stock.getPurpose());
        data.setBatchNo(stock.getBatchNo());
        data.setExpiryDate(stock.getExpiryDate());
        data.setCreatedBy(stock.getCreatedBy());
        data.setCachedQuantity(stock.getCachedQuantity());
        data.setDateCreated(stock.getTransactionDate());
        data.setLastUpdated(stock.getReceivedAt());
        data.setStatus(stock.getStatus());
        data.setDiscount(stock.getDiscount() !=null ? stock.getDiscount() : BigDecimal.ZERO);
        data.setTax(stock.getTax() != null? stock.getTax() : BigDecimal.ZERO);
        data.setCostPrice(stock.getItem().getRate());

        double qty = stock.getPurpose() == MovementPurpose.Returns ? stock.getQuantity()* -1 : stock.getQuantity();
        data.setFormattedQuantity(qty);
        data.setFormattedTotal(stock.getPrice().multiply(BigDecimal.valueOf(qty)));
        data.setFixedQuantity(stock.getQuantity());

        data.setTotalExclusive(data.getFormattedTotal().subtract(data.getDiscount()));
        data.setTotalAmount(data.getTotalExclusive().add(data.getTax()));

        data.setTotalExclusive(data.getFormattedTotal().subtract(data.getDiscount()));
        data.setTotalAmount(data.getTotalExclusive().add(data.getTax()));

        return data;
    }
    private BigDecimal toDefault(BigDecimal val){
        return val!=null ? val : BigDecimal.ZERO;
    }
    /*
    Perpetual Inventory
        If perpetual inventory system is enabled, additional costs will be booked in "Expense Included In Valuation" account.
    Periodic
        Do not post stocks
     */
}
