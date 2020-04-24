package io.smarthealth.accounting.billing.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillData {

    private Long id;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate billingDate;
    private String patientNumber;
    private String patientName;
    private String visitNumber;
    private String billNumber;
    private String transactionId; //Receipt n. or Invoice No
    private String paymentMode;
    private Double balance;
    private Double amount;
    private Double taxes;
    private Double discount;
    private String reference;
    private String otherDetails;
    private Boolean walkinFlag;
    @Enumerated(EnumType.STRING)
    private BillStatus status;

    private List<BillItemData> billItems;

}
