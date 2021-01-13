package io.smarthealth.accounting.invoice.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.infrastructure.lang.Constants;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class InvoiceData {

    private Long id;
    private Long payerId;
    private String payer;
    private String terms;
    private Long schemeId;
    private String scheme;
    private String patientNumber;
    private String patientName;
    private String visitNumber;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate visitDate;
    private String memberNumber;
    private String memberName;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate invoiceDate;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate dueDate;
    private String number;  //invoice number
    private BigDecimal amount;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal balance;
    private String transactionNo;
    private String createdBy;
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;
    private String notes;
    private List<InvoiceItemData> invoiceItems = new ArrayList<>();
    private List<InvoiceReceipt> invoicePayments=new ArrayList<>();
    @ApiModelProperty(hidden=true, required=false)
    private String state;
    private Boolean awaitingSmart;
    @ApiModelProperty(hidden=true, required=false)
    private Long age;
    @ApiModelProperty(hidden=true, required=false)
    private String diagnosis;
    @ApiModelProperty(hidden=true, required=false)
    private String idNumber;
    @ApiModelProperty(hidden=true, required=false)
    private BigDecimal total= BigDecimal.ZERO;
    
    @ApiModelProperty(hidden=true, required=false)
    private BigDecimal excess = BigDecimal.ZERO;

     private Boolean capitation=Boolean.FALSE;
    @ApiModelProperty(hidden=true, required=false)
     private String visitType;
}
