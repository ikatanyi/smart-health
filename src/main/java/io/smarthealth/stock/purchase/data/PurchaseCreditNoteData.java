package io.smarthealth.stock.purchase.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

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
    private List<PurchaseCreditNoteItem> items = new ArrayList<>();
}
