package io.smarthealth.accounting.invoice.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.accounting.payment.data.CopaymentData;
import io.smarthealth.accounting.payment.domain.Copayment;
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
     private Boolean capitation=Boolean.FALSE;
}
