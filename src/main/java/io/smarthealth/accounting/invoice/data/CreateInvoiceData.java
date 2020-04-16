package io.smarthealth.accounting.invoice.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class CreateInvoiceData {
  
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate date;
    
    private String notes;
    private String billNumber;
    private String patientNumber;
    private Double subTotal;
    private Double discount;
    private Double taxes;
    private Double total;
    private List<DebtorData> payers=new ArrayList<>();
    private List<CreateInvoiceItemData> items = new ArrayList<>();
 
}
