package io.smarthealth.accounting.doctors.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.doctors.domain.DoctorInvoice.TransactionType;
import io.smarthealth.infrastructure.lang.Constants;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data 
public class DoctorInvoiceData  {

    private Long id;
    private Long doctorId;
    private String visitNumber;
    private String doctorName;
    @ApiModelProperty(required=false,hidden=true)
    private String staffNumber;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate invoiceDate;
    private String invoiceNumber;
    @ApiModelProperty(required=false,hidden=true)
    private String referenceNumber;
      
    private String patientNumber;
    
    private String patientName; 
    
     private Long serviceId;
    private String serviceName;
    private String serviceCode;
      
    private Boolean paid;
    private BigDecimal amount;
    private BigDecimal balance;
    private String paymentMode;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private String transactionId;
}
