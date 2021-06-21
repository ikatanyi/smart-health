package io.smarthealth.accounting.billing.data;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class VoidReceipt {
    @NotBlank(message = "Receipt No. is required")
    private String receiptNo;
    @NotBlank(message = "Comments/Reasons for voiding the receipt is required")
    private String comments;
    private String shiftNo;
}
