package io.smarthealth.debtor.claim.remittance.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.debtor.claim.remittance.domain.RemittanceOld;
import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class RemitanceData {
    
    private Long id;
    @NotNull
    private Long payerId;
    private String payerName;
    private Long bankAccountId;
    private String bankName;
    @ApiModelProperty(required = false, hidden = true)
    private String bankAccountNumber;
    private Double amount;
    private String paymentMode;
    private String paymentCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate transactionDate;
    private String receiptNo;
    private String transactionId;
    private Double balance;
    
    private String termsName;
    private String notes;
    private String termsDescription;
    private Integer creditDays;
    private Boolean termsActive;
    
    public static RemittanceOld map(RemitanceData data) {
        RemittanceOld remitance = new RemittanceOld();
        remitance.setAmount(data.getAmount());
        remitance.setNotes(data.getNotes());
        remitance.setBalance(data.getBalance());
        remitance.setReceiptNo(data.getReceiptNo());
        remitance.setPaymentMode(data.getPaymentMode());
        remitance.setPaymentCode(data.getPaymentCode());
        remitance.setTransactionDate(data.getTransactionDate());
        return remitance;
    }
    
    public static RemitanceData map(RemittanceOld remitance) {
        RemitanceData data = new RemitanceData();
        data.setId(remitance.getId());
        data.setAmount(remitance.getAmount());
        data.setBalance(remitance.getBalance());
        data.setReceiptNo(remitance.getReceiptNo());
        data.setPaymentCode(remitance.getPaymentCode());
        data.setPaymentMode(remitance.getPaymentMode());
        data.setNotes(remitance.getNotes());
        data.setTransactionDate(remitance.getTransactionDate());
        data.setTransactionId(remitance.getTransactionId());
        if (remitance.getPayer() != null) {
            data.setPayerName(remitance.getPayer().getPayerName());
            data.setPayerId(remitance.getPayer().getId());
            if (remitance.getPayer().getPaymentTerms() != null) {
                data.setTermsName(remitance.getPayer().getPaymentTerms().getTermsName());
                data.setTermsDescription(remitance.getPayer().getPaymentTerms().getDescription());
                data.setCreditDays(remitance.getPayer().getPaymentTerms().getCreditDays());
                data.setTermsActive(remitance.getPayer().getPaymentTerms().getActive());
            }
        }
        if (remitance.getBankAccount() != null) {
            data.setBankAccountId(remitance.getBankAccount().getId());
            if (remitance.getBankAccount().getBank() != null) {
                data.setBankName(remitance.getBankAccount().getBank().getBankName());
            }
            data.setBankAccountNumber(remitance.getBankAccount().getAccountNumber());
        }
        return data;
    }
}
