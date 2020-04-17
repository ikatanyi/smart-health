package io.smarthealth.accounting.invoice.data;

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
public class CreateInvoice {
  
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate date;
    
    private String notes;
    private String visitNumber;
    private String patientNumber;
    private BigDecimal subTotal;
    private BigDecimal discount;
    private BigDecimal taxes;
    private BigDecimal total;
    private List<InvoicePayer> payers=new ArrayList<>();
    private List<CreateInvoiceItem> items = new ArrayList<>();
 
}
