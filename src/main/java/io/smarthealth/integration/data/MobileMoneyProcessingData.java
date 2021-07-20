package io.smarthealth.integration.data;

import io.smarthealth.administration.mobilemoney.domain.MobileMoneyProvider;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Data
public class MobileMoneyProcessingData {
    @NotNull(message = "Phone number cannot be empty")
    private String phoneNumber;
    private String visitNumber;

    @NotNull(message = "Patient number cannot be empty")
    private String patientNumber;

    @Enumerated(EnumType.STRING)
    private MobileMoneyProvider provider;
    private String ShiftNumber;
    private String ReceiptNumber;
}
