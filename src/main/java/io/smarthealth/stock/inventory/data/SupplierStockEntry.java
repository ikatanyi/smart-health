package io.smarthealth.stock.inventory.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.stock.inventory.domain.enumeration.PurchaseType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class SupplierStockEntry {

    private Long id;
     @NotNull
    private Long storeId;
    private String store;
    
    @Enumerated(EnumType.STRING)
    private PurchaseType purchaseType;
    
    private String orderNumber;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate transactionDate;
    private String transactionId;
    
    private Long supplierId;
    private String supplierName;

    private String supplierInvoiceNumber;
     @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate supplierInvoiceDate;
      @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate supplierInvoiceDueDate;

    private String deliveredBy;
    private String receivedBy;
    
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal taxes = BigDecimal.ZERO;
    private BigDecimal amount = BigDecimal.ZERO;

    private List<SupplierStockItem> items = new ArrayList<>();

    public BigDecimal getStockTotals() {
        if (!items.isEmpty()) {
            BigDecimal totals = items.stream()
                    .map(x -> x.getAmount())
                    .reduce(BigDecimal.ZERO, (x, y) -> x.add(y));

            return totals;
        }
        return BigDecimal.ZERO;
    }
       public BigDecimal getDiscountTotals() {
        if (!items.isEmpty()) {
            BigDecimal totals = items.stream()
                    .map(x -> x.getDiscount())
                    .reduce(BigDecimal.ZERO, (x, y) -> x.add(y));

            return totals;
        }
        return BigDecimal.ZERO;
    }
          public BigDecimal getTaxTotals() {
        if (!items.isEmpty()) {
            BigDecimal totals = items.stream()
                    .map(x -> x.getTax())
                    .reduce(BigDecimal.ZERO, (x, y) -> x.add(y));

            return totals;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getNetAmount() {
        return (getStockTotals().add(getTaxes())).subtract(getDiscountTotals());
    }
}
