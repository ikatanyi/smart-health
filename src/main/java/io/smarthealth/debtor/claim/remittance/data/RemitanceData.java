package io.smarthealth.debtor.claim.remittance.data;

import io.smarthealth.debtor.claim.remittance.domain.Remitance;
import io.smarthealth.debtor.claim.remittance.domain.enumeration.PaymentMode;
import java.time.LocalDate;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    @NotNull
    private Long  bankId;
    private String bankName;
    private String bankAccountNumber;
    private Double amount;
    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;
    private String paymentCode;
    private LocalDate transactionDate;
    private String receiptNo;
    private Double balance;

    private String termsName;
    private String termsDescription;
    private Integer creditDays;
    private Boolean termsActive;

    public static Remitance map(RemitanceData data) {
        Remitance remitance = new Remitance();
        remitance.setAmount(data.getAmount());
        remitance.setBalance(data.getBalance());
        remitance.setReceiptNo(data.getReceiptNo());
        remitance.setPaymentMode(data.getPaymentMode());
        remitance.setPaymentCode(data.getPaymentCode());
        remitance.setTransactionDate(data.getTransactionDate());
        return remitance;
    }

    public static RemitanceData map(Remitance remitance) {
        RemitanceData data = new RemitanceData();
        data.setAmount(remitance.getAmount());
        data.setBalance(remitance.getBalance());
        data.setReceiptNo(remitance.getReceiptNo());
        data.setPaymentCode(remitance.getPaymentCode());
        data.setPaymentMode(remitance.getPaymentMode());
        data.setTransactionDate(remitance.getTransactionDate());
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
        if(remitance.getBankAccount()!=null){
            data.setBankId(remitance.getBankAccount().getId());
            if(remitance.getBankAccount().getBank()!=null){
                data.setBankName(remitance.getBankAccount().getBank().getBankName());
            }
            data.setBankAccountNumber(remitance.getBankAccount().getAccountNumber());
        }
        return data;
    }
}
