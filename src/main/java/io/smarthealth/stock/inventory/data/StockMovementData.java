package io.smarthealth.stock.inventory.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.stock.inventory.domain.StockMovement;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Stock Entry
 *
 * @author Kelsas
 */
@Data
public class StockMovementData {

    private Long id;
    private Long storeId;
    private String store;
    private Long itemId;
    private String itemCode;
    private String item;
    private Long uomId;
    private String uom;
    private double receiving;
    private double issuing;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String referenceNumber; //ref LPO,supplier, patient no
    private String deliveryNumber; //GRN| transaction reference
    private String transactionNumber; //auto generated ST-2019-00002
    private String journalNumber;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDate transactionDate;
    private MovementType moveType;
    private MovementPurpose purpose;

    public static StockMovementData map(StockMovement stock) {
        StockMovementData data = new StockMovementData();
        data.setId(stock.getId());
        if (stock.getStore() != null) {
            data.setStoreId(stock.getStore().getId());
            data.setStore(stock.getStore().getStoreName());
        }
        if (stock.getItem() != null) {
            data.setItemId(stock.getItem().getId());
            data.setItemCode(stock.getItem().getItemCode());
            data.setItem(stock.getItem().getItemName());
        }
        if (stock.getUom() != null) {
            data.setUomId(stock.getUom().getId());
            data.setUom(stock.getUom().getName());
        }
        data.setReceiving(stock.getReceiving());
        data.setIssuing(stock.getIssuing());
        data.setUnitPrice(stock.getUnitPrice());
        data.setTotalAmount(stock.getTotalAmount());
        data.setReferenceNumber(stock.getReferenceNumber());
        data.setDeliveryNumber(stock.getDeliveryNumber());
        data.setTransactionNumber(stock.getTransactionNumber());
        data.setJournalNumber(stock.getJournalNumber());
        data.setTransactionDate(stock.getTransactionDate());
        data.setMoveType(stock.getMoveType());
        data.setPurpose(stock.getPurpose());

        return data;
    }

    /*
    Perpetual Inventory
        If perpetual inventory system is enabled, additional costs will be booked in "Expense Included In Valuation" account.
    Periodic
        Do not post stocks
     */
}
