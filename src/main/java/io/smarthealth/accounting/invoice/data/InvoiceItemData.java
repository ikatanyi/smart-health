package io.smarthealth.accounting.invoice.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class InvoiceItemData implements Serializable {
      private Long id;
      @JsonFormat(pattern = Constants.DATE_PATTERN)
      private LocalDate date;
      private  String invoiceNo;
      @JsonFormat(pattern = Constants.DATE_PATTERN)
      private LocalDate invoiceDate;
      private Long itemId;
      private String itemCode;
      private String item;
      private Double quantity;
      private BigDecimal price;
      private BigDecimal discount;
      private BigDecimal tax;
      private BigDecimal amount;
      private String transactionId;
      private Long servicePointId;
      private String servicePoint;
      @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
      private LocalDateTime billingDatetime;
      
}
