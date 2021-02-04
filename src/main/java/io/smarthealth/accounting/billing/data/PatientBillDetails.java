package io.smarthealth.accounting.billing.data;

import io.smarthealth.clinical.visit.data.enums.VisitEnum.VisitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientBillDetails {
    private String billNo; // this can be visit_number
    private String patientNumber;
    private String patientName;
    private String visitNumber;
    private LocalDateTime visitDate;
    private VisitType visitType;
    private BigDecimal totalBillAmount;
    private BigDecimal totalAmountPaid;
    private BigDecimal balance;
}
