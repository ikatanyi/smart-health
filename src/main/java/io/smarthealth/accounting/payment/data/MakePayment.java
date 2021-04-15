package io.smarthealth.accounting.payment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.payment.domain.enumeration.PayeeType;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MakePayment {

    public enum PayThrough {
        Cash,
        Bank
    }

    private Long id;
    private String voucherNo; //payment id
    private Long creditorId;
    private String creditor;
    private PayeeType creditorType;
    private String paymentMethod; // if cash give options for petty cash or undeposited funds
    private BigDecimal amount;

    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate date;
    private String description;
    private String referenceNumber;
    private String transactionNo;
    private String currency;
    private BigDecimal bankCharge;
    private String taxAccount;
    private String taxAccountNumber;

    private PayChannel paymentChannel;
    private String expenseAccount;
    private String expenseAccountName;

    private List<BillToPay> invoices = new ArrayList<>();
}
