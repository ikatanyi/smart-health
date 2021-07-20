package io.smarthealth.stock.purchase.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kelsas
 */
@Data
public class PurchaseCreditNoteData {

    private Long supplierId;
    private String supplier;

    private Long storeId;
    private String store;

    private String creditNoteNumber;
    private String invoiceNumber;
    private String transactionId;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate creditDate;
    private BigDecimal amount;
    private String reason;
    private String supplierReference;
    private String vatReference;
    private String documentNumber;
    private String invoiceDocumentNo;
    private List<PurchaseCreditNoteItemData> items = new ArrayList<>();

    public BigDecimal getTotalAmount(){
        return items.stream()
                .map(PurchaseCreditNoteItemData::getTotal)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalDiscount(){
        return items.stream()
                .map(PurchaseCreditNoteItemData::getDiscount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }
    public BigDecimal getTotalVat(){
        return items.stream()
                .map(PurchaseCreditNoteItemData::getTax)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }
}
